# BMA rebuild — master TODO

The living checklist for finishing the rebuild. Scaffold items that are **done**
are checked; everything unchecked is yours to take over. Grouped so you can pick
up one module at a time.

Legend: `[x]` done in scaffold · `[ ]` to do · 🔒 = needs your LIC authorization.

---

## 0. Foundations

- [x] Postgres schema for the recovered data model (`db/migrations/0001_init.sql`)
- [x] Go backend skeleton: config, router, response envelope, dummy store
- [x] Kotlin app skeleton: theme, nav, reference feature (Policies)
- [x] Responsive Compose design system: spacing scale (`ui/theme/Dimens.kt`),
      adaptive layout helpers (`ui/components/Layout.kt` — width classes,
      adaptive columns, max-width container), shared state views
      (loading/error/empty), reusable content (StatusChip, StatTile, DetailRow).
      Dashboard + Policies reflow 1→2→3/4 columns by width.
- [x] LIC-portal boundary defined + stubbed (`backend/internal/licportal`)
- [x] Recovered `bmaservices.in` endpoint surface → `docs/API_SURFACE.md`
- [x] APK release CI on push to main (`.github/workflows/android-release.yml`)
- [ ] Add Postgres store: `store.NewPostgres(dsn)` implementing `store.Store`
      (pgx or sqlc). Replace `store.NewDummyStore()` in `main.go`.
- [ ] Seed migration `0002_seed_bonus_rates.sql` from `bmaextra.s3db:BonusRate`
      (68 rows; normalise the wide plan columns into `bonus_rates`).
- [ ] Auth: `/auth/register|login|otp|mpin|forgot-password`, JWT/session,
      replace `agentID()` stub with real claims; add auth middleware.
- [ ] CI: `go build/test`, `golangci-lint`, Android `assembleDebug`.

## 1. Backend API (`bmaservices.in` parity)

The exact request/response shapes for your existing `bmaservices.in` server live
in the app's 308 managed assemblies (`lib/<abi>/libassemblies.*.blob.so`).

- [ ] Decompile the app-owned assemblies and extract, for each `bmaservices.in`
      call: path, method, request fields, response fields, auth header.
      (REA: `inspect-managed-members` for `ldstr` URL/string literals, then
      `decompile`. The extraction step was blocked once in this sandbox by an
      automated safety check — re-run it locally, or approve the Bash rule.)
- [ ] For each recovered endpoint, add a typed Go handler + `ApiService` method.
- [x] CRUD list endpoints (dummy): agencies, clients, policies, prospects, commission.
- [ ] CRUD create/update/delete for each of the above.
- [ ] `/api/v1/dashboard` summary endpoint (feeds the KPI row).

## 2. Clients module

Original: `client_list`, `client_profile`, `client_manage`, `family_details`,
`family_list`, `family_manage`, `family_report`, `family_upload`.

- [x] `GET /clients` (dummy)
- [ ] Client CRUD + search/filter (`filter_client`)
- [ ] Family grouping (`family_head_id`) + family report
- [ ] Kotlin: ClientsScreen / ClientDetailScreen / edit (copy policies slice)

## 3. Policies module

Original: `policy_list/details/manage/status/tracker`, `add_policy`, `proposal`,
`proposal_data`, `renewal`, `fupupdate`, `nbupdate`.

- [x] `GET /policies` (dummy) + PoliciesScreen (reference slice)
- [ ] Policy CRUD, proposal entry, renewal tracking, FUP/NB updates
- [ ] Policy status 🔒 (portal) — calls `licportal.FetchPremiumStatus`
- [ ] Kotlin: detail + add/edit screens

## 4. Leads / pipeline module

Original: `prospect*`, `leads_list/view`, `lead_extractor*`, `recruitment`,
`refer`, `do_lead`, `do_import`.

- [x] `GET /prospects` (dummy)
- [ ] Pipeline stages, follow-ups, prospect CRUD
- [ ] Lead import; recruitment tracking
- [ ] Kotlin: LeadsScreen + pipeline UI

## 5. Commission module

Original: `commission`, `commission_list/report/form`, `due_report`, `latefee`.

- [x] `GET /commission?month&year` (dummy, with line details)
- [ ] Commission import/parse → `commission_summaries` + detail tables
- [ ] Commission statement 🔒 (portal) — `licportal.FetchCommissionStatement`
- [ ] Due report, late-fee calc
- [ ] Kotlin: CommissionScreen (summary + premium/recovery lines)

## 6. Reports module

Original: `business_report`, `business_card`, `cm_report*`, `summary`,
`agency_ranking`, `report_result`, `any_status`, `status`, `due_report`.

- [ ] Report generation endpoints + PDF export (`pdf_*`)
- [ ] Kotlin: ReportsScreen with filters (`filter_ranking`)

## 7. Agencies / agents module

Original: `agency_*` (details/list/manage/ranking/import/download, +clia),
`agent_contact`, `agent_import`, `agent_data_new`.

- [x] `GET /agencies` (dummy)
- [ ] Agency CRUD, ranking, import; agent contacts
- [ ] Kotlin: AgenciesScreen

## 8. Marketing module

Original: `ai_*` (poster/video/studio/prompt/result/history), `poster*`,
`image*`, `invitation*`, `certificate*`, `digicards`, `custom_greeting`,
`video_*`.

- [ ] Content/template storage + share endpoints
- [ ] AI generation: wire to your chosen provider (decide: server-side only)
- [ ] Kotlin: MarketingScreen (templates → share)

## 9. Tools / settings module

Original: `settings`, `notifications`, `data_backup/download*`, `document`,
`view_document`, `pdf_cal/calendar/share`, `update_mobile`, `whats_new`.

- [ ] Settings, profile, data export/backup (your data only)
- [ ] Push notifications (FCM) — server + client
- [ ] Kotlin: ToolsScreen + Settings

## 10. 🔒 LIC portal integration (you own this)

Everything here is intentionally **not** implemented. It must be built by you
under your LIC authorization, ideally against an official LIC API.

- [ ] Implement `licportal.Client` (replace `StubClient`):
      `FetchPremiumStatus`, `FetchCommissionStatement`, `FetchRevivalQuote`,
      plus any others you need.
- [ ] Credential handling: pass the agent's portal secret per-request
      (`X-Portal-Secret`); **never** persist it (schema has no column for it).
- [ ] `portal_jobs` table bookkeeping (request/status/result), async workers.
- [ ] Decide client-side vs server-side execution and document your authority.

### Explicitly dropped from the rebuild (do not reintroduce without need)

- Device SMS reading (`SMSReceiver`, `ReadMessages`, `ReadFintsoMessages`)
- Notification listener (`bma.NotificationListener`)
- System-overlay / keyguard / boot-receiver automation
- The broad permission set from the original manifest (rebuild ships INTERNET only)

---

## Progress tracking

Keep this file as the source of truth. When you finish an item, check it here in
the same PR that implements it.
