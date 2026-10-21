set -euo pipefail

cd "$(dirname "$0")"

KEEP_DAYS="${KEEP_DAYS:-14}"
BACKUP_DIR="backups"
COMPOSE="docker compose -f docker-compose.prod.yml"
FILE="$BACKUP_DIR/hgest-$(date +%Y%m%d-%H%M%S).sql.gz"

mkdir -p "$BACKUP_DIR"

$COMPOSE exec -T db sh -c 'pg_dump -U "$POSTGRES_USER" -d hgest --clean --if-exists' | gzip > "$FILE.tmp"
mv "$FILE.tmp" "$FILE"
echo "$(date -Is) backup written: $FILE ($(du -h "$FILE" | cut -f1))"

find "$BACKUP_DIR" -name 'hgest-*.sql.gz' -mtime +"$KEEP_DAYS" -delete

if [ -n "${RCLONE_REMOTE:-}" ]; then
  rclone copy "$FILE" "$RCLONE_REMOTE"
  echo "$(date -Is) copied to $RCLONE_REMOTE"
fi
