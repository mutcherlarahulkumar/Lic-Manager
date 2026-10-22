# Seeds

## ⚠️ Real seeds contain secrets — keep them out of git

A production backup seed (e.g. the Balam export) contains **live credentials and
PII**: bcrypt password hashes, plaintext passwords, auth tokens, MPIN, PAN,
device IDs. Do **not** commit it. Put real seed files in `db/seed/local/`, which
is gitignored. Rotate any passwords/tokens that have been shared in a file.

## Layout

```
db/legacy/schema.sql   legacy table definitions (no data — safe to commit)
db/seed/local/*.sql    your real seeds (gitignored, never committed)
db/scripts/seed.sh     loads legacy schema + a seed file into $DATABASE_URL
```

## Load a seed into your database

```bash
export DATABASE_URL="postgres://USER:PASS@HOST:5432/DB?sslmode=require"
db/scripts/seed.sh db/seed/local/balam_seed.sql
```

This applies `db/legacy/schema.sql` first (so the data-only seed has tables to
land in), then loads the seed.

## Important: this is the LEGACY schema

The backup export targets the original app's tables
(`agent`, `client`, `family`, `policies`, `plans`, `commission`, `sb`,
`fuphistory`, `fupupdate`, …) — **not** the rebuild schema in
`db/migrations/`. So:

- Loading it stands your real data up in Postgres under the legacy tables.
- The rebuild app (Go backend + Kotlin) uses the new schema, so a follow-up
  **ETL** step is needed to map legacy → rebuild tables. Tracked in
  `docs/REBUILD_TODO.md` / `docs/APPROACH.md`.

Column types in `db/legacy/schema.sql` were inferred from the seed's values
(e.g. money columns are TEXT because the export quotes them); adjust them if
your full backup needs stricter types.
