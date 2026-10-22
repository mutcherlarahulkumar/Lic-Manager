#!/usr/bin/env bash
# Load a data seed into $DATABASE_URL.
#
# The Balam backup seed is DATA-ONLY and targets the LEGACY schema, so this
# first applies db/legacy/schema.sql (CREATE TABLE IF NOT EXISTS) and then the
# seed file you pass in.
#
# Usage:
#   DATABASE_URL="postgres://…?sslmode=require" db/scripts/seed.sh path/to/seed.sql
#
# SECURITY: real seeds contain credentials/PII. Keep them in db/seed/local/
# (gitignored) and never commit them. Rotate any exposed passwords/tokens.
set -euo pipefail

: "${DATABASE_URL:?set DATABASE_URL to your Postgres connection string}"
SEED="${1:?usage: seed.sh <seed.sql>}"
[ -f "$SEED" ] || { echo "seed file not found: $SEED"; exit 1; }
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

echo "Applying legacy schema ($ROOT/legacy/schema.sql)..."
psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -q -f "$ROOT/legacy/schema.sql"

echo "Loading seed: $SEED"
psql "$DATABASE_URL" -v ON_ERROR_STOP=1 -q -f "$SEED"
echo "Seed loaded."
