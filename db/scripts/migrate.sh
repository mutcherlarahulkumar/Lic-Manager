#!/usr/bin/env bash
# Apply all SQL migrations in db/migrations (in filename order) to $DATABASE_URL.
# Idempotent: migrations use CREATE ... IF NOT EXISTS, so re-running is safe.
#
# Usage: DATABASE_URL="postgres://user:pass@host:5432/db?sslmode=require" db/scripts/migrate.sh
set -euo pipefail

: "${DATABASE_URL:?set DATABASE_URL to your Postgres connection string}"
DIR="$(cd "$(dirname "$0")/.." && pwd)/migrations"

echo "Applying migrations from $DIR"
for f in "$DIR"/*.sql; do
  [ -e "$f" ] || { echo "no migrations found"; exit 0; }
  echo "  -> $(basename "$f")"
  psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -q -f "$f"
done
echo "Migrations applied."
