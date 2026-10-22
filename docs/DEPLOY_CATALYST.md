# Deploy the Go backend to Zoho Catalyst (AppSail)

The backend is a long-running HTTP server, so the right Catalyst product is
**AppSail** (Catalyst's app-hosting service for your own stack), not Functions.
The database stays on your external cloud Postgres — you've already put the
connection string in the environment; AppSail just needs it set as an env var.

## Prerequisites

- A Zoho Catalyst project (console.catalyst.zoho.com / .in for the India DC).
- Catalyst CLI:
  ```bash
  npm install -g zcatalyst-cli
  catalyst --version
  catalyst login        # opens browser auth for your Zoho account
  ```
- Run all of this from **your own machine** — this cloud sandbox's network
  blocks Zoho hosts (the Catalyst MCP/CLI can't reach them from here).

## The code is already AppSail-ready

AppSail injects the port to listen on via `X_ZOHO_CATALYST_LISTEN_PORT`. The
server honors it (see `backend/internal/config/config.go`), binds on all
interfaces, and exposes `GET /healthz`. No code change needed to deploy.

## One-time init

From the repo root:

```bash
cd backend
catalyst init
#  - Select your Catalyst project.
#  - Choose the "AppSail" (App Hosting) component.
#  - Stack: Go.
#  - Build command:  go build -o server ./cmd/server
#  - Run command:    ./server
#  - Leave the port as the injected default (do not hard-code it).
```

`catalyst init` writes the AppSail config (e.g. `app-config.json` /
`catalyst.json`) into the project — let the CLI generate these rather than
hand-writing them, so the schema matches your CLI version. Commit whatever it
generates (they contain no secrets).

## Set environment variables

In the Catalyst console → your AppSail app → **Configuration / Environment
Variables**, add:

| Key | Value |
|-----|-------|
| `DATABASE_URL` | your external Postgres connection string (`…?sslmode=require`) |
| `BMA_ENV` | `prod` |

Do **not** commit these. AppSail sets `X_ZOHO_CATALYST_LISTEN_PORT` itself.

## Deploy

```bash
catalyst deploy
```

The CLI runs the build command, uploads the app, and starts it with the run
command. After it finishes it prints the public URL. Verify:

```bash
curl https://<your-appsail-url>/healthz      # -> {"ok":true,"data":{"status":"up"}}
curl https://<your-appsail-url>/api/v1/policies
```

## Point the Android app at it

In `android/app/build.gradle.kts`, set the release `API_BASE_URL` to your
AppSail URL (keep the trailing slash), then build a release APK.

## Notes

- `render.yaml` is left in the repo as an alternative host; ignore it if you're
  going with Catalyst.
- Migrations/seeds still run against your Postgres directly with the scripts in
  `db/scripts/` — Catalyst only hosts the Go process, not the database.
- If `catalyst deploy` reports the app isn't becoming healthy, check that the
  run command is `./server`, that the build produced the `server` binary, and
  that `DATABASE_URL` is set; tail logs with `catalyst logs` (or the console's
  AppSail logs).
