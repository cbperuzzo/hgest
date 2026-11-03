#!/usr/bin/env python3
"""
End-to-end smoke tests for the hgest API.

Standard library only: urllib for HTTP, tkinter for the window.

    python tests/api_test.py              # GUI
    python tests/api_test.py --cli        # terminal
    python tests/api_test.py --cli --base http://localhost:8080 --user admin --password secret

The run needs a reachable API and one account with SUPERVISOR or above.
It creates its own worker and service, each named with a timestamp, so the
suite can be run repeatedly without cleaning the database in between.
"""

import argparse
import json
import queue
import sys
import threading
import time
import urllib.error
import urllib.request
from datetime import date

DEFAULT_BASE = "http://localhost:8080"
DEFAULT_USER = "admin"
DEFAULT_PASSWORD = "admin"

PASS = "PASS"
FAIL = "FAIL"
SKIP = "SKIP"


class Skip(Exception):
    """Raised when a step cannot run because an earlier one did not finish."""


class Api:
    def __init__(self, base):
        self.base = base.rstrip("/")

    def call(self, method, path, body=None, token=None):
        """Return (status, parsed_body). Never raises for HTTP error codes."""
        data = json.dumps(body).encode("utf-8") if body is not None else None
        request = urllib.request.Request(self.base + path, data=data, method=method)

        if data is not None:
            request.add_header("Content-Type", "application/json")
        if token:
            request.add_header("Authorization", "Bearer " + token)

        try:
            with urllib.request.urlopen(request, timeout=15) as response:
                return response.status, _parse(response.read())
        except urllib.error.HTTPError as error:
            return error.code, _parse(error.read())
        except urllib.error.URLError as error:
            raise AssertionError("could not reach %s (%s)" % (self.base, error.reason))


def _parse(raw):
    text = raw.decode("utf-8", errors="replace").strip()
    if not text:
        return None
    try:
        return json.loads(text)
    except ValueError:
        return text


def items(body):
    """Pull the rows out of a paged response, whatever shape it arrives in.

    Spring Data has serialized pages more than one way across versions, so
    accept a bare list, {"content": [...]} and the HAL-style wrapper.
    """
    if body is None:
        return []
    if isinstance(body, list):
        return body
    if isinstance(body, dict):
        if isinstance(body.get("content"), list):
            return body["content"]
        embedded = body.get("_embedded")
        if isinstance(embedded, dict):
            for value in embedded.values():
                if isinstance(value, list):
                    return value
    return []


def expect(status, wanted, body=None):
    """Assert a status code. `wanted` is one code or a tuple of them."""
    codes = wanted if isinstance(wanted, tuple) else (wanted,)
    if status not in codes:
        raise AssertionError(
            "expected %s, got %s%s"
            % ("/".join(str(c) for c in codes), status, "" if body is None else " body=%r" % (body,))
        )


def denied(status, body=None):
    """Spring Security answers 401 or 403 depending on the entry point."""
    expect(status, (401, 403), body)


# --------------------------------------------------------------------------
# the steps
# --------------------------------------------------------------------------

STEPS = []


def step(name):
    def register(function):
        STEPS.append((name, function))
        return function
    return register


def need(ctx, *keys):
    for key in keys:
        if ctx.get(key) is None:
            raise Skip("missing %s from an earlier step" % key)


def login(api, name, password):
    status, body = api.call("POST", "/api/auth/login/remember",
                            {"alias": name, "password": password})
    expect(status, 200, body)
    if not isinstance(body, dict) or "access" not in body or "refresh" not in body:
        raise AssertionError("login did not return a token pair: %r" % (body,))
    return body["access"], body["refresh"]


@step("reject a wrong password")
def _(api, ctx):
    status, body = api.call("POST", "/api/auth/login/remember",
                            {"alias": ctx["admin_user"], "password": "definitely-not-the-password"})
    expect(status, 401, body)
    return "401 as expected"


@step("log in as admin")
def _(api, ctx):
    ctx["admin_access"], ctx["admin_refresh"] = login(api, ctx["admin_user"], ctx["admin_password"])
    return "got an access and a refresh token"


@step("log in through the deprecated endpoint")
def _(api, ctx):
    status, body = api.call("POST", "/api/auth/login",
                            {"alias": ctx["admin_user"], "password": ctx["admin_password"]})
    expect(status, 200, body)
    if not isinstance(body, dict) or "token" not in body:
        raise AssertionError("expected a token, got %r" % (body,))
    return "still issues an access token"


@step("reject a request with no token")
def _(api, ctx):
    status, body = api.call("GET", "/api/user/self")
    denied(status, body)
    return "refused with %s" % status


@step("read my own user")
def _(api, ctx):
    need(ctx, "admin_access")
    status, body = api.call("GET", "/api/user/self", token=ctx["admin_access"])
    expect(status, 200, body)
    if body.get("userName") != ctx["admin_user"]:
        raise AssertionError("expected %r, got %r" % (ctx["admin_user"], body.get("userName")))
    ctx["admin_role"] = body.get("role")
    return "%s, role %s" % (body.get("userName"), body.get("role"))


@step("create a worker")
def _(api, ctx):
    need(ctx, "admin_access")
    status, body = api.call("POST", "/api/user/create",
                            {"name": ctx["worker_user"], "password": ctx["worker_password"], "role": "WORKER"},
                            token=ctx["admin_access"])
    expect(status, 200, body)
    return "created %s" % ctx["worker_user"]


@step("reject a duplicate name on create")
def _(api, ctx):
    need(ctx, "admin_access")
    status, body = api.call("POST", "/api/user/create",
                            {"name": ctx["worker_user"], "password": ctx["worker_password"], "role": "WORKER"},
                            token=ctx["admin_access"])
    expect(status, 400, body)
    return "400: %s" % body


@step("reject an unknown role on create")
def _(api, ctx):
    need(ctx, "admin_access")
    status, body = api.call("POST", "/api/user/create",
                            {"name": ctx["worker_user"] + "_bogus", "password": "whatever", "role": "WIZARD"},
                            token=ctx["admin_access"])
    expect(status, 400, body)
    return "400: %s" % body


@step("find the worker by name")
def _(api, ctx):
    need(ctx, "admin_access")
    status, body = api.call("GET", "/api/user?name=" + ctx["worker_user"], token=ctx["admin_access"])
    expect(status, 200, body)
    rows = [row for row in items(body) if row.get("userName") == ctx["worker_user"]]
    if not rows:
        raise AssertionError("search did not return the new worker: %r" % (body,))
    ctx["worker_id"] = rows[0]["id"]
    return "id %s" % ctx["worker_id"]


@step("read the worker by id")
def _(api, ctx):
    need(ctx, "admin_access", "worker_id")
    status, body = api.call("GET", "/api/user/%s" % ctx["worker_id"], token=ctx["admin_access"])
    expect(status, 200, body)
    if body.get("role") != "WORKER":
        raise AssertionError("expected WORKER, got %r" % body.get("role"))
    return "%s, active=%s" % (body.get("userName"), body.get("active"))


@step("404 for a user that does not exist")
def _(api, ctx):
    need(ctx, "admin_access")
    status, body = api.call("GET", "/api/user/99999999", token=ctx["admin_access"])
    expect(status, 404, body)
    return "404 as expected"


@step("update a user without sending a role (bug 5)")
def _(api, ctx):
    need(ctx, "admin_access", "worker_id")
    renamed = ctx["worker_user"] + "_renamed"
    status, body = api.call("PATCH", "/api/user/update/%s" % ctx["worker_id"],
                            {"name": renamed}, token=ctx["admin_access"])
    expect(status, 200, body)
    ctx["worker_user"] = renamed
    return "renamed to %s without a 500" % renamed


@step("reject a duplicate name on update (bug 9)")
def _(api, ctx):
    need(ctx, "admin_access", "worker_id")
    status, body = api.call("PATCH", "/api/user/update/%s" % ctx["worker_id"],
                            {"name": ctx["admin_user"]}, token=ctx["admin_access"])
    expect(status, 400, body)
    return "400: %s" % body


@step("log in as the worker")
def _(api, ctx):
    need(ctx, "worker_id")
    ctx["worker_access"], ctx["worker_refresh"] = login(api, ctx["worker_user"], ctx["worker_password"])
    return "worker session established"


@step("reject an @ in a user name")
def _(api, ctx):
    need(ctx, "admin_access")
    status, body = api.call("POST", "/api/user/create",
                            {"name": ctx["worker_user"] + "@example.com", "password": ctx["worker_password"], "role": "WORKER"},
                            token=ctx["admin_access"])
    expect(status, 400, body)
    return "400: %s" % body


@step("set a user's email")
def _(api, ctx):
    need(ctx, "admin_access", "worker_id")
    email = ctx["worker_user"].lower() + "@example.com"
    status, body = api.call("PATCH", "/api/user/update/%s" % ctx["worker_id"],
                            {"email": email}, token=ctx["admin_access"])
    expect(status, 200, body)
    ctx["worker_email"] = email
    status, body = api.call("GET", "/api/user/%s" % ctx["worker_id"], token=ctx["admin_access"])
    expect(status, 200, body)
    if body.get("email") != email:
        raise AssertionError("expected %r, got %r" % (email, body.get("email")))
    return email


@step("log in with the email, in any case")
def _(api, ctx):
    need(ctx, "worker_email")
    login(api, ctx["worker_email"].upper(), ctx["worker_password"])
    return "logged in as %s" % ctx["worker_email"].upper()


@step("reject a wrong password with the email")
def _(api, ctx):
    need(ctx, "worker_email")
    status, body = api.call("POST", "/api/auth/login/remember",
                            {"alias": ctx["worker_email"], "password": "definitely-not-the-password"})
    expect(status, 401, body)
    return "401 as expected"


@step("reject a duplicate email on create")
def _(api, ctx):
    need(ctx, "admin_access", "worker_email")
    status, body = api.call("POST", "/api/user/create",
                            {"name": ctx["worker_user"] + "_2", "password": ctx["worker_password"],
                             "role": "WORKER", "email": ctx["worker_email"].upper()},
                            token=ctx["admin_access"])
    expect(status, 400, body)
    return "400: %s" % body


@step("reject an invalid email on create")
def _(api, ctx):
    need(ctx, "admin_access")
    status, body = api.call("POST", "/api/user/create",
                            {"name": ctx["worker_user"] + "_3", "password": ctx["worker_password"],
                             "role": "WORKER", "email": "not-an-email"},
                            token=ctx["admin_access"])
    expect(status, 400, body)
    return "400 as expected"


@step("change my own email")
def _(api, ctx):
    need(ctx, "worker_access", "worker_email")
    email = "self." + ctx["worker_email"]
    status, body = api.call("POST", "/api/user/self/update",
                            {"email": email, "currentPassword": ctx["worker_password"]},
                            token=ctx["worker_access"])
    expect(status, 200, body)
    login(api, email, ctx["worker_password"])
    status, body = api.call("POST", "/api/auth/login/remember",
                            {"alias": ctx["worker_email"], "password": ctx["worker_password"]})
    expect(status, 401, body)
    ctx["worker_email"] = email
    return "the new email logs in, the old one does not"


@step("a blank email removes it")
def _(api, ctx):
    need(ctx, "admin_access", "worker_id", "worker_email")
    status, body = api.call("PATCH", "/api/user/update/%s" % ctx["worker_id"],
                            {"email": ""}, token=ctx["admin_access"])
    expect(status, 200, body)
    status, body = api.call("POST", "/api/auth/login/remember",
                            {"alias": ctx["worker_email"], "password": ctx["worker_password"]})
    expect(status, 401, body)
    login(api, ctx["worker_user"], ctx["worker_password"])
    return "the email no longer logs in, the user name still does"


@step("a worker cannot list users")
def _(api, ctx):
    need(ctx, "worker_access")
    status, body = api.call("GET", "/api/user", token=ctx["worker_access"])
    denied(status, body)
    return "refused with %s" % status


@step("a worker cannot list services (bug 10)")
def _(api, ctx):
    need(ctx, "worker_access")
    status, body = api.call("GET", "/api/service", token=ctx["worker_access"])
    denied(status, body)
    return "refused with %s" % status


@step("open a service")
def _(api, ctx):
    need(ctx, "admin_access")
    status, body = api.call("POST", "/api/service/open",
                            {"title": ctx["service_title"], "description": "opened by the test suite",
                             "os": ctx["service_os"]},
                            token=ctx["admin_access"])
    expect(status, 200, body)
    return "opened %s" % ctx["service_title"]


@step("find the service by title")
def _(api, ctx):
    need(ctx, "admin_access")
    status, body = api.call("GET", "/api/service/search?title=" + ctx["service_title"],
                            token=ctx["admin_access"])
    expect(status, 200, body)
    rows = [row for row in items(body) if row.get("title") == ctx["service_title"]]
    if not rows:
        raise AssertionError("search did not return the new service: %r" % (body,))
    ctx["service_id"] = rows[0]["id"]
    return "id %s" % ctx["service_id"]


@step("read the service by id")
def _(api, ctx):
    need(ctx, "admin_access", "service_id")
    status, body = api.call("GET", "/api/service/%s" % ctx["service_id"], token=ctx["admin_access"])
    expect(status, 200, body)
    if body.get("closed") is not False:
        raise AssertionError("expected an open service, got closed=%r" % body.get("closed"))
    return "open, os=%s" % body.get("os")


@step("filter services by os")
def _(api, ctx):
    need(ctx, "admin_access", "service_id")
    status, body = api.call("GET", "/api/service/by-os?os=%s" % ctx["service_os"], token=ctx["admin_access"])
    expect(status, 200, body)
    if not any(row.get("id") == ctx["service_id"] for row in items(body)):
        raise AssertionError("service not in the by-os results")
    return "found it"


@step("list services with filters")
def _(api, ctx):
    need(ctx, "admin_access", "service_id")
    status, body = api.call("GET", "/api/service?closed=false", token=ctx["admin_access"])
    expect(status, 200, body)
    if not any(row.get("id") == ctx["service_id"] for row in items(body)):
        raise AssertionError("service not in the filtered list")
    return "%s row(s)" % len(items(body))


@step("update a service")
def _(api, ctx):
    need(ctx, "admin_access", "service_id")
    status, body = api.call("POST", "/api/service/update/%s" % ctx["service_id"],
                            {"description": "edited by the test suite"}, token=ctx["admin_access"])
    expect(status, 200, body)

    status, body = api.call("GET", "/api/service/%s" % ctx["service_id"], token=ctx["admin_access"])
    expect(status, 200, body)
    if body.get("description") != "edited by the test suite":
        raise AssertionError("description did not change: %r" % body.get("description"))
    return "description updated"


@step("no open shift before opening one")
def _(api, ctx):
    need(ctx, "worker_access")
    status, body = api.call("GET", "/api/shift/self/open", token=ctx["worker_access"])
    expect(status, 404, body)
    return "404 as expected"


@step("open a shift")
def _(api, ctx):
    need(ctx, "worker_access", "service_id")
    status, body = api.call("POST", "/api/shift/self/open",
                            {"serviceId": ctx["service_id"], "date": ctx["today"], "time": "08:00:00",
                             "description": ctx["shift_description"], "segment": "morning"},
                            token=ctx["worker_access"])
    expect(status, 200, body)
    return "opened for %s 08:00" % ctx["today"]


@step("the open shift keeps its description (bug 6)")
def _(api, ctx):
    need(ctx, "worker_access")
    status, body = api.call("GET", "/api/shift/self/open", token=ctx["worker_access"])
    expect(status, 200, body)
    ctx["shift_id"] = body.get("id")
    if body.get("description") != ctx["shift_description"]:
        raise AssertionError("description was dropped: %r" % body.get("description"))
    return "id %s, description kept" % ctx["shift_id"]


@step("the open shift records registeredOpening (item 11)")
def _(api, ctx):
    need(ctx, "worker_access")
    status, body = api.call("GET", "/api/shift/self/open", token=ctx["worker_access"])
    expect(status, 200, body)
    if not body.get("registeredOpening"):
        raise AssertionError("registeredOpening is empty: %r" % (body,))
    if body.get("registeredClosing"):
        raise AssertionError("registeredClosing should still be empty")
    return "registeredOpening=%s" % body.get("registeredOpening")


@step("reject a second open shift")
def _(api, ctx):
    need(ctx, "worker_access", "service_id")
    status, body = api.call("POST", "/api/shift/self/open",
                            {"serviceId": ctx["service_id"], "date": ctx["today"], "time": "09:00:00",
                             "description": "should not be created", "segment": "morning"},
                            token=ctx["worker_access"])
    expect(status, 400, body)
    return "400: %s" % body


@step("list my own shifts")
def _(api, ctx):
    need(ctx, "worker_access", "shift_id")
    status, body = api.call("GET", "/api/shift/self", token=ctx["worker_access"])
    expect(status, 200, body)
    if not any(row.get("id") == ctx["shift_id"] for row in items(body)):
        raise AssertionError("own shift missing from the list")
    return "%s row(s)" % len(items(body))


@step("an admin can read the shift by id")
def _(api, ctx):
    need(ctx, "admin_access", "shift_id")
    status, body = api.call("GET", "/api/shift/%s" % ctx["shift_id"], token=ctx["admin_access"])
    expect(status, 200, body)
    if body.get("userId") != ctx["worker_id"]:
        raise AssertionError("shift belongs to %r, expected %r" % (body.get("userId"), ctx["worker_id"]))
    return "belongs to the worker"


@step("close the shift")
def _(api, ctx):
    need(ctx, "worker_access", "shift_id")
    status, body = api.call("POST", "/api/shift/self/close/%s" % ctx["shift_id"],
                            {"date": ctx["today"], "time": "17:00:00"}, token=ctx["worker_access"])
    expect(status, 200, body)
    return "closed at 17:00"


@step("the closed shift totals 540 minutes and records registeredClosing")
def _(api, ctx):
    need(ctx, "admin_access", "shift_id")
    status, body = api.call("GET", "/api/shift/%s" % ctx["shift_id"], token=ctx["admin_access"])
    expect(status, 200, body)
    if body.get("totalMinutes") != 540:
        raise AssertionError("expected 540 minutes, got %r" % body.get("totalMinutes"))
    if not body.get("registeredClosing"):
        raise AssertionError("registeredClosing is empty")
    return "540 minutes, registeredClosing=%s" % body.get("registeredClosing")


@step("reject closing the shift twice")
def _(api, ctx):
    need(ctx, "worker_access", "shift_id")
    status, body = api.call("POST", "/api/shift/self/close/%s" % ctx["shift_id"],
                            {"date": ctx["today"], "time": "18:00:00"}, token=ctx["worker_access"])
    expect(status, 400, body)
    return "400: %s" % body


@step("no open shift after closing")
def _(api, ctx):
    need(ctx, "worker_access")
    status, body = api.call("GET", "/api/shift/self/open", token=ctx["worker_access"])
    expect(status, 404, body)
    return "404 as expected"


@step("an admin can list shifts by user")
def _(api, ctx):
    need(ctx, "admin_access", "worker_id", "shift_id")
    status, body = api.call("GET", "/api/shift/user/%s" % ctx["worker_id"], token=ctx["admin_access"])
    expect(status, 200, body)
    if not any(row.get("id") == ctx["shift_id"] for row in items(body)):
        raise AssertionError("shift missing from the per-user list")
    return "%s row(s)" % len(items(body))


@step("an admin can total the minutes for a user")
def _(api, ctx):
    need(ctx, "admin_access", "worker_id")
    status, body = api.call("GET", "/api/shift/user/%s/total" % ctx["worker_id"], token=ctx["admin_access"])
    expect(status, 200, body)
    if not isinstance(body, int) or body < 540:
        raise AssertionError("expected at least 540, got %r" % (body,))
    return "%s minutes" % body


@step("an admin can list shifts by service")
def _(api, ctx):
    need(ctx, "admin_access", "service_id", "shift_id")
    status, body = api.call("GET", "/api/shift/service/%s" % ctx["service_id"], token=ctx["admin_access"])
    expect(status, 200, body)
    if not any(row.get("id") == ctx["shift_id"] for row in items(body)):
        raise AssertionError("shift missing from the per-service list")
    return "%s row(s)" % len(items(body))


@step("self update rejects a wrong current password")
def _(api, ctx):
    need(ctx, "worker_access")
    status, body = api.call("POST", "/api/user/self/update",
                            {"newPassword": "irrelevant", "currentPassword": "wrong"},
                            token=ctx["worker_access"])
    expect(status, 400, body)
    return "400 as expected"


@step("self update rejects a missing current password (bug 7)")
def _(api, ctx):
    need(ctx, "worker_access")
    status, body = api.call("POST", "/api/user/self/update",
                            {"newPassword": "irrelevant"}, token=ctx["worker_access"])
    expect(status, 400, body)
    return "400 instead of a 500"


@step("self update rejects a name that is taken (bug 9)")
def _(api, ctx):
    need(ctx, "worker_access")
    status, body = api.call("POST", "/api/user/self/update",
                            {"name": ctx["admin_user"], "currentPassword": ctx["worker_password"]},
                            token=ctx["worker_access"])
    expect(status, 400, body)
    return "400: %s" % body


@step("self update changes my password")
def _(api, ctx):
    need(ctx, "worker_access")
    new_password = ctx["worker_password"] + "_v2"
    status, body = api.call("POST", "/api/user/self/update",
                            {"newPassword": new_password, "currentPassword": ctx["worker_password"]},
                            token=ctx["worker_access"])
    expect(status, 200, body)

    ctx["worker_password"] = new_password
    ctx["worker_access"], ctx["worker_refresh"] = login(api, ctx["worker_user"], new_password)
    return "changed and logged in again"


@step("two devices can be logged in at once (option B)")
def _(api, ctx):
    need(ctx, "worker_id")
    phone_access, phone_refresh = login(api, ctx["worker_user"], ctx["worker_password"])
    desk_access, desk_refresh = login(api, ctx["worker_user"], ctx["worker_password"])

    status, body = api.call("POST", "/api/auth/refresh", {"refreshToken": phone_refresh})
    expect(status, 200, body)
    phone_refresh = body["refresh"]

    status, body = api.call("POST", "/api/auth/refresh", {"refreshToken": desk_refresh})
    expect(status, 200, body)
    desk_refresh = body["refresh"]

    ctx["phone_refresh"] = phone_refresh
    ctx["desk_refresh"] = desk_refresh
    return "both sessions refreshed independently"


@step("logging out one device leaves the other alone")
def _(api, ctx):
    need(ctx, "phone_refresh", "desk_refresh")
    status, body = api.call("POST", "/api/auth/logout", {"refreshToken": ctx["phone_refresh"]})
    expect(status, 204, body)

    status, body = api.call("POST", "/api/auth/refresh", {"refreshToken": ctx["phone_refresh"]})
    expect(status, 401, body)

    status, body = api.call("POST", "/api/auth/refresh", {"refreshToken": ctx["desk_refresh"]})
    expect(status, 200, body)
    ctx["desk_refresh"] = body["refresh"]
    return "phone logged out, desk still refreshing"


@step("logout/all ends every session")
def _(api, ctx):
    need(ctx, "worker_id")
    _, first = login(api, ctx["worker_user"], ctx["worker_password"])
    _, second = login(api, ctx["worker_user"], ctx["worker_password"])

    status, body = api.call("POST", "/api/auth/logout/all", {"refreshToken": first})
    expect(status, 204, body)

    status, body = api.call("POST", "/api/auth/refresh", {"refreshToken": first})
    expect(status, 401, body)

    status, body = api.call("POST", "/api/auth/refresh", {"refreshToken": second})
    expect(status, 401, body)
    return "both sessions ended"


@step("replaying a rotated token kills every session (bug 13)")
def _(api, ctx):
    need(ctx, "worker_id")
    _, original = login(api, ctx["worker_user"], ctx["worker_password"])
    _, other_device = login(api, ctx["worker_user"], ctx["worker_password"])

    status, body = api.call("POST", "/api/auth/refresh", {"refreshToken": original})
    expect(status, 200, body)
    rotated = body["refresh"]

    status, body = api.call("POST", "/api/auth/refresh", {"refreshToken": original})
    expect(status, 401, body)

    status, body = api.call("POST", "/api/auth/refresh", {"refreshToken": rotated})
    expect(status, 401, body)

    status, body = api.call("POST", "/api/auth/refresh", {"refreshToken": other_device})
    expect(status, 401, body)
    return "reuse revoked the rotated chain and the other device"


@step("disable the worker")
def _(api, ctx):
    need(ctx, "admin_access", "worker_id")
    status, body = api.call("PATCH", "/api/user/disable/%s" % ctx["worker_id"], token=ctx["admin_access"])
    expect(status, 200, body)
    return "disabled"


@step("a disabled user cannot log in")
def _(api, ctx):
    need(ctx, "worker_id")
    status, body = api.call("POST", "/api/auth/login/remember",
                            {"alias": ctx["worker_user"], "password": ctx["worker_password"]})
    expect(status, 401, body)
    return "401 as expected"


@step("close the service")
def _(api, ctx):
    need(ctx, "admin_access", "service_id")
    status, body = api.call("POST", "/api/service/close/%s" % ctx["service_id"], token=ctx["admin_access"])
    expect(status, 200, body)
    return "closed"


@step("reject closing the service twice")
def _(api, ctx):
    need(ctx, "admin_access", "service_id")
    status, body = api.call("POST", "/api/service/close/%s" % ctx["service_id"], token=ctx["admin_access"])
    expect(status, 400, body)
    return "400: %s" % body


# --------------------------------------------------------------------------
# runner
# --------------------------------------------------------------------------

def new_context(user, password):
    suffix = str(int(time.time()))
    return {
        "admin_user": user,
        "admin_password": password,
        "worker_user": "test_worker_" + suffix,
        "worker_password": "worker_pw_" + suffix,
        "service_title": "test_service_" + suffix,
        "service_os": int(suffix[-6:]),
        "shift_description": "opened by the test suite",
        "today": date.today().isoformat(),
    }


def run(base, user, password, report):
    """Run every step, handing (index, name, outcome, detail) to `report`."""
    api = Api(base)
    ctx = new_context(user, password)
    tally = {PASS: 0, FAIL: 0, SKIP: 0}

    for index, (name, function) in enumerate(STEPS):
        try:
            detail = function(api, ctx) or ""
            outcome = PASS
        except Skip as reason:
            outcome, detail = SKIP, str(reason)
        except AssertionError as error:
            outcome, detail = FAIL, str(error)
        except Exception as error:                  # report it, never crash the run
            outcome, detail = FAIL, "%s: %s" % (type(error).__name__, error)

        tally[outcome] += 1
        report(index, name, outcome, detail)

    return tally


# --------------------------------------------------------------------------
# terminal front end
# --------------------------------------------------------------------------

def main_cli(args):
    width = max(len(name) for name, _ in STEPS)

    def report(index, name, outcome, detail):
        print("%-4s %-*s  %s" % (outcome, width, name, detail))

    print("hgest API tests against %s as %s\n" % (args.base, args.user))
    tally = run(args.base, args.user, args.password, report)
    print("\n%s passed, %s failed, %s skipped" % (tally[PASS], tally[FAIL], tally[SKIP]))
    return 1 if tally[FAIL] else 0


# --------------------------------------------------------------------------
# window
# --------------------------------------------------------------------------

def main_gui(args):
    import tkinter as tk
    from tkinter import ttk

    root = tk.Tk()
    root.title("hgest API tests")
    root.geometry("980x620")

    form = ttk.Frame(root, padding=10)
    form.pack(fill="x")

    base_var = tk.StringVar(value=args.base)
    user_var = tk.StringVar(value=args.user)
    password_var = tk.StringVar(value=args.password)

    for column, (label, var, width, hide) in enumerate([
        ("Base URL", base_var, 30, False),
        ("User", user_var, 16, False),
        ("Password", password_var, 16, True),
    ]):
        ttk.Label(form, text=label).grid(row=0, column=column * 2, sticky="w",
                                         padx=(0 if column == 0 else 12, 4))
        entry = ttk.Entry(form, textvariable=var, width=width, show="*" if hide else "")
        entry.grid(row=0, column=column * 2 + 1, sticky="w")

    run_button = ttk.Button(form, text="Run tests")
    run_button.grid(row=0, column=6, padx=(16, 0))

    summary = ttk.Label(root, text="idle", padding=(10, 0))
    summary.pack(fill="x")

    body = ttk.Frame(root, padding=10)
    body.pack(fill="both", expand=True)

    table = ttk.Treeview(body, columns=("outcome", "name", "detail"), show="headings", height=18)
    table.heading("outcome", text="")
    table.heading("name", text="Step")
    table.heading("detail", text="Detail")
    table.column("outcome", width=60, anchor="center", stretch=False)
    table.column("name", width=360, stretch=False)
    table.column("detail", width=480)
    table.pack(side="left", fill="both", expand=True)

    scroll = ttk.Scrollbar(body, orient="vertical", command=table.yview)
    scroll.pack(side="right", fill="y")
    table.configure(yscrollcommand=scroll.set)

    table.tag_configure(PASS, foreground="#1a7f37")
    table.tag_configure(FAIL, foreground="#b42318")
    table.tag_configure(SKIP, foreground="#8a6d00")

    detail_box = tk.Text(root, height=6, wrap="word")
    detail_box.pack(fill="x", padx=10, pady=(0, 10))

    def show_detail(_event=None):
        selected = table.selection()
        detail_box.delete("1.0", "end")
        if selected:
            values = table.item(selected[0], "values")
            detail_box.insert("1.0", "%s - %s\n\n%s" % (values[0], values[1], values[2]))

    table.bind("<<TreeviewSelect>>", show_detail)

    updates = queue.Queue()

    def start():
        run_button.state(["disabled"])
        table.delete(*table.get_children())
        detail_box.delete("1.0", "end")
        summary.configure(text="running %s steps against %s ..." % (len(STEPS), base_var.get()))

        def worker():
            tally = run(base_var.get(), user_var.get(), password_var.get(),
                        lambda *row: updates.put(("row",) + row))
            updates.put(("done", tally))

        threading.Thread(target=worker, daemon=True).start()

    def drain():
        while True:
            try:
                message = updates.get_nowait()
            except queue.Empty:
                break

            if message[0] == "row":
                _, _index, name, outcome, detail = message
                table.insert("", "end", values=(outcome, name, detail), tags=(outcome,))
                table.yview_moveto(1.0)
            else:
                tally = message[1]
                summary.configure(text="%s passed, %s failed, %s skipped"
                                       % (tally[PASS], tally[FAIL], tally[SKIP]))
                run_button.state(["!disabled"])

        root.after(80, drain)

    run_button.configure(command=start)
    root.after(80, drain)
    root.mainloop()
    return 0


def parse_args(argv):
    parser = argparse.ArgumentParser(description="End-to-end tests for the hgest API.")
    parser.add_argument("--base", default=DEFAULT_BASE, help="API base URL (default %s)" % DEFAULT_BASE)
    parser.add_argument("--user", default=DEFAULT_USER, help="an account with SUPERVISOR or above")
    parser.add_argument("--password", default=DEFAULT_PASSWORD, help="that account's password")
    parser.add_argument("--cli", action="store_true", help="run in the terminal instead of a window")
    parser.add_argument("--list", action="store_true", help="list the steps and exit")
    return parser.parse_args(argv)


if __name__ == "__main__":
    options = parse_args(sys.argv[1:])
    if options.list:
        for number, (step_name, _) in enumerate(STEPS, start=1):
            print("%2d. %s" % (number, step_name))
        sys.exit(0)
    sys.exit(main_cli(options) if options.cli else main_gui(options))
