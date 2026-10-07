# API tests

End-to-end tests for the running hgest API. Standard library only, so there is
nothing to install: Python 3 and a reachable API are enough.

## Running

Start Postgres and the app first, then:

```
py -3 tests/api_test.py --user <admin> --password <password>
```

That opens a small window: check the base URL and credentials, press **Run
tests**, and each step reports as it finishes. Click a row to read its detail.

For a terminal run, add `--cli`:

```
py -3 tests/api_test.py --cli --user <admin> --password <password>
```

`--cli` exits non-zero if anything failed, so it works in a script or a CI job.
`--list` prints the steps without running them.

Options: `--base` (default `http://localhost:8080`), `--user`, `--password`.

## What it needs

One existing account with SUPERVISOR or above. The `hgest.bootstrap.username`
and `hgest.bootstrap.password` user is the obvious one to use.

Everything else is created by the run: a worker, a service and a shift, each
named with a timestamp. So the suite can be run repeatedly without clearing the
database, at the cost of leaving test rows behind. The worker it creates ends up
disabled and the service closed, which is what the last few steps check.

## Reading the results

- **PASS** the step did what was expected.
- **FAIL** an assertion did not hold, with the expected and actual status.
- **SKIP** an earlier step did not produce something this one needs, so it could
  not run. Fix the first failure and these usually clear up.

The steps run in order and share state, so a failure early on tends to skip a
lot of what follows.
