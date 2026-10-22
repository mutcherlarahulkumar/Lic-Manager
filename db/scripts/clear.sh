#!/usr/bin/env bash
# Clear DATA from all app tables (TRUNCATE) WITHOUT dropping the schema.
# Keeps table structure; removes every row. DESTRUCTIVE.
#
# Guard: refuses to run unless CONFIRM=yes is set.
# Usage: DATABASE_URL=... CONFIRM=yes db/scripts/clear.sh
set -euo pipefail

: "${DATABASE_URL:?set DATABASE_URL}"
[ "${CONFIRM:-no}" = "yes" ] || { echo "Refusing: set CONFIRM=yes to truncate all tables."; exit 1; }

echo "Truncating all data in public schema (structure kept)..."
psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -q <<'SQL'
DO $$
DECLARE r RECORD; stmt TEXT := '';
BEGIN
  FOR r IN SELECT tablename FROM pg_tables WHERE schemaname = 'public' LOOP
    stmt := stmt || format('%I,', r.tablename);
  END LOOP;
  IF length(stmt) > 0 THEN
    EXECUTE 'TRUNCATE TABLE ' || rtrim(stmt, ',') || ' RESTART IDENTITY CASCADE';
  END IF;
END $$;
SQL
echo "All rows cleared."
