# Setup & operations

Everything you need to run, deploy, and operate the rebuild.

## 1. Prerequisites

| Tool | Version | For |
|------|---------|-----|
| Go | 1.23+ | backend |
| PostgreSQL client (`psql`) | 14+ | DB scripts |
| JDK | 17 | Android build |
| Android Studio | latest | Android app |
| A cloud Postgres | — | your DB (connection string) |

## 2. Secrets / environment

| Name | Where | Value |
|------|-------|-------|
| `DATABASE_URL` | GitHub repo secret **and** Render env | `postgres://USER:PASS@HOST:5432/DB?sslmode=require` |
| `BLISS_API_BASE_URL` | Render env (optional) | your existing `https://bmaservices.in` base, only if proxying during cut-over |

Add a GitHub secret: **Settings → Secrets and variables → Actions → New
repository secret**. Never commit connection strings.

## 3. Run locally

```bash
# Backend (dummy data, no DB required yet)
cd backend && go run ./cmd/server        # http://localhost:8080
curl localhost:8080/healthz

# Database (point at your cloud Postgres)
export DATABASE_URL="postgres://…?sslmode=require"
db/scripts/migrate.sh                     # create/update schema

# Android
#   open android/ in Android Studio, let it sync, run on an emulator.
#   Debug build talks to http://10.0.2.2:8080 (the host's localhost).
```

## 4. Database operations

Run anytime, locally or from the **Actions tab → "Database" → Run workflow**:

| Action | Local command | What it does |
|--------|---------------|--------------|
| migrate | `db/scripts/migrate.sh` | apply migrations (safe, idempotent) |
| clear | `CONFIRM=yes db/scripts/clear.sh` | TRUNCATE all tables (keeps schema) — **destructive** |
| reset | `CONFIRM=yes db/scripts/reset.sh` | drop schema + re-migrate — **destructive** |

- **Auto-sync:** pushing a change under `db/migrations/**` to `main` runs
  migrate automatically (`.github/workflows/db-sync.yml`).
- Manual clear/reset in CI require typing the **repository name** in the confirm
  box (a safety gate against accidental wipes).

## 5. Deploy the backend to Render

1. Push to `main` (the blueprint `render.yaml` lives at the repo root).
2. Render Dashboard → **New → Blueprint** → select this repo.
3. On the `bma-backend` service, set `DATABASE_URL` (and optionally
   `BLISS_API_BASE_URL`) as environment variables.
4. Deploy. Health check is `/healthz`. `autoDeploy` redeploys on each push to
   `main`.

The Android app is a client; it is **not** deployed. Point its release build at
the Render URL (`app/build.gradle.kts` → `API_BASE_URL`).

## 6. CI / release

| Workflow | Trigger | Does |
|----------|---------|------|
| `android-release.yml` | push to `main` under `android/**`, or manual | build APK, publish a GitHub Release |
| `db-sync.yml` | push to `main` under `db/migrations/**`, or manual | migrate / clear / reset the DB |

### Why the Android runs failed (verified from the logs) and the fixes

1. **Run 1** invoked `./gradlew`, but no Gradle **wrapper** was committed →
   "no such file or directory".
2. **Run 2** failed earlier, at the **Set up Android SDK** step:
   `android-actions/setup-android@v3` tried to install the obsolete `tools` SDK
   package — `Warning: Failed to find package 'tools'` → `sdkmanager` exit 1.
   The job never reached Gradle.
3. A **warning** on every run: `actions/checkout@v4`, `setup-java@v4` and
   `setup-android@v3` target the deprecated Node 20.

**Fixes applied:** dropped `setup-android` entirely (the `ubuntu-latest` runner
already has the Android SDK at `$ANDROID_HOME`); accept licenses defensively;
build with Gradle provisioned by `gradle/actions/setup-gradle@v4` (no committed
wrapper). First-party actions bumped to Node 24 versions (`checkout@v5`,
`setup-java@v5`) to clear the deprecation warning. Any residual Node 20 warning
would come only from third-party actions until they publish Node 24 releases.

> Note: the Android project could not be compiled in the build sandbox used to
> create it (no Android SDK there). The first CI run is the real compile — if it
> surfaces errors, open `android/` in Android Studio once, let it sync/fix, and
> push. For a *signed* release APK, add `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`,
> `KEY_ALIAS`, `KEY_PASSWORD` secrets and switch the build to `assembleRelease`.

## 7. Versions

Pinned in `android/build.gradle.kts` (AGP 8.7.3, Kotlin 2.1.0, Compose BOM
2024.12.01, KSP 2.1.0-1.0.29), `android/gradle/wrapper` (Gradle 8.14.3), and
`backend/go.mod` (Go 1.23, chi v5). Bump as newer stable releases land; keep the
Kotlin and Compose-compiler-plugin versions equal.
