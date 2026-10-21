# hgest API

Spring Boot REST API (Java 21) with PostgreSQL.

## Local development

```bash
cp .env.example .env            # fill in the values
docker compose up -d --build    # API on http://localhost:8080, DB on localhost:3030
```

## Production deploy (single VPS)

Stack: Caddy (HTTPS) → API → Postgres, all in `docker-compose.prod.yml`. Only ports 80/443 are exposed.

1. **Server:** a Linux VPS with Docker installed. Firewall: allow only 22, 80 and 443.
2. **DNS:** point an `A` record for your API domain (e.g. `api.example.com`) at the server's IP.
3. **Code and config:**
   ```bash
   git clone <repo> /opt/hgest && cd /opt/hgest
   cp .env.example .env
   ```
   In `.env`, set strong values for `POSTGRES_PASSWORD`, `JWT_SECRET` (32+ chars) and `HGEST_BOOTSTRAP_PASSWORD`, and set `API_DOMAIN`.
4. **Start:**
   ```bash
   docker compose -f docker-compose.prod.yml up -d --build
   ```
   Caddy fetches the HTTPS certificate on first start. Check `https://<API_DOMAIN>/api/user/self` answers 403 (running, no token).
5. **Backups:** nightly via cron (`crontab -e`):
   ```
   0 3 * * * /opt/hgest/backup.sh >> /opt/hgest/backups/backup.log 2>&1
   ```
   Dumps go to `backups/` and are kept 14 days (`KEEP_DAYS`). Set `RCLONE_REMOTE` to also copy them off the server, and test a restore once (see the end of `backup.sh`).
6. **Update:**
   ```bash
   git pull && docker compose -f docker-compose.prod.yml up -d --build
   ```

The mobile app's `EXPO_PUBLIC_API_URL` (in `eas.json` for builds) should then be `https://<API_DOMAIN>`.
