#!/usr/bin/env bash
# Reset the database: DROP the public schema entirely and re-apply migrations.
# This wipes BOTH data and structure, then rebuilds a clean schema. DESTRUCTIVE.
#
# Guard: refuses to run unless CONFIRM=yes is set.
# Usage: DATABASE_URL=... CONFIRM=yes db/scripts/reset.sh
set -euo pipefail

: "${DATABASE_URL:?set DATABASE_URL}"
[ "${CONFIRM:-no}" = "yes" ] || { echo "Refusing: set CONFIRM=yes to drop and rebuild the schema."; exit 1; }
HERE="$(cd "$(dirname "$0")" && pwd)"

echo "Dropping and recreating public schema..."
psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -q <<'SQL'
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
SQL

echo "Re-applying migrations..."
"$HERE/migrate.sh"
echo "Database reset complete."
