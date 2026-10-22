# Takeover plan — optimised order of work

The most efficient path to finish the rebuild yourself. Do phases in order;
within a phase, items are roughly parallelisable. Check `docs/REBUILD_TODO.md`
for the full per-module checklist; this file is the *sequence* and the *why*.

## Phase 0 — Get it running (½ day)
1. Create a cloud Postgres; copy its connection string.
2. Add `DATABASE_URL` as a GitHub secret and a Render env var (see SETUP.md §2).
3. `db/scripts/migrate.sh` → schema is live.
4. Deploy backend to Render (SETUP.md §5). Confirm `/healthz` is green.
5. Open `android/` in Android Studio, run on an emulator against the backend.
   Fix any first-compile issues the build sandbox couldn't catch; push so the
   Android CI goes green and cuts your first APK release.

## Phase 1 — Lock the backend contract (highest leverage)
The whole app depends on this, so do it first.
6. For each endpoint in `docs/API_SURFACE.md`, decompile its caller with REA and
   fill the request/response template. Start with the ones every screen needs:
   - `ApiService.FetchCookies` → design your auth: issue a JWT/session on login;
     replace cookie passing.
   - `/ptdata/agentdata.php?token=` and `/ptdata/getm*.php` → agent + master data.
   - `/cloud/uploaddata.php` + `datadownload.php` → backup/restore.
7. Turn each documented endpoint into a typed Go route + handler + `ApiService`
   method on the app. Swap `store.DummyStore` for a Postgres store
   (`store.NewPostgres`), one module at a time.
8. Fix the two original bugs noted in API_SURFACE.md (double-`.in` host; plain HTTP).

## Phase 2 — Build modules top-down by value
Do the modules in this order (revenue/ops first):
9. **Clients** → **Policies** → **Commission** → **Leads** → **Reports**
   → **Agencies** → **Marketing** → **Tools**.
   For each: Postgres queries → Go CRUD routes → Kotlin screen (copy the
   `policies/` slice: Screen + ViewModel + repo + ApiService method). Use the
   shared components in `ui/components` so every screen stays consistent.

## Phase 3 — LIC portal integration (you own this)
10. Implement `licportal.Client` against your authorized LIC access (ideally an
    official API). Keep credentials per-request (`X-Portal-Secret`), never
    stored. Wire `portal_jobs` for async fetches. This stays isolated from the
    rest of the app by design.

## Phase 4 — Harden & ship
11. Auth middleware everywhere; real `agentID()` from the token.
12. Seed `bonus_rates` (migration `0002`). Add indexes as queries demand.
13. Signed release APK (add keystore secrets; switch CI to `assembleRelease`).
14. Observability: request logging, error reporting; replace the original's
    `/manage/crashlog.php` with your crash tool.

## Guardrails / decisions already made for you
- **Clean UI, not a clone:** 8 modules, not 150 flat buttons. Keep it that way.
- **Minimal permissions:** app ships INTERNET only; add one back only when a
  screen truly needs it, with a runtime rationale. Do NOT reintroduce SMS/
  notification harvesting.
- **Exact money math:** all amounts are `NUMERIC`/`BigDecimal`, never float.
- **One response envelope:** `{ok,data,error,meta}` for every endpoint.

## What's already done (don't redo)
- Postgres schema, Go backend skeleton (builds + runs on dummy data),
  responsive Compose design system + Dashboard/Policies, LIC-portal stub,
  API surface map, DB scripts + workflow, Render blueprint, Android release CI.
  See `docs/REBUILD_TODO.md`.
