# Lic-Manager — BMA rebuild

A clean re-implementation of the **BLISS Mobile Agent** app on a modern stack:

- **Android app** — Kotlin + Jetpack Compose (`android/`)
- **Backend** — Go (`backend/`)
- **Database** — PostgreSQL (`db/`)

The original app was .NET MAUI (C#). This rebuild keeps the data model and
feature set but ships a clean architecture and a fresh UI (not a copy of the
original). It runs today on **dummy data** so you can develop top to bottom
before wiring Postgres and your integrations.

## Quick start

```bash
# 1. Backend (dummy data, no DB needed yet)
cd backend && go run ./cmd/server          # serves http://localhost:8080
curl localhost:8080/api/v1/policies

# 2. Database (when ready)
createdb bma && psql "$DATABASE_URL" -f db/migrations/0001_init.sql

# 3. Android: open android/ in Android Studio, run on an emulator
#    (debug build points at http://10.0.2.2:8080)
```

## Where things are

| Path                     | What                                             |
|--------------------------|--------------------------------------------------|
| `docs/ARCHITECTURE.md`   | How the pieces fit; what was recovered from APK  |
| `docs/REBUILD_TODO.md`   | **Master checklist** — what's done, what's yours |
| `db/migrations/`         | Postgres schema (recovered data model)           |
| `backend/`               | Go API (dummy store + stubbed LIC portal)        |
| `android/`               | Kotlin app (reference feature: Policies)         |

## Scope note

The original app's **LIC-portal automation** and **device SMS/notification
reading** are **not** reimplemented. They exist only as the stubbed
`backend/internal/licportal` interface returning dummy data. Implement them
yourself under your LIC authorization — ideally against an official LIC API.
See `docs/REBUILD_TODO.md § 10`.
