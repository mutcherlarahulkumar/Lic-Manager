# BMA Android app (Kotlin + Jetpack Compose)

Clean rebuild of the BLISS Mobile Agent client. This is a **scaffold**: it
compiles-ready structure, a working theme/navigation, and one fully-wired
reference feature (Policies). You build the rest by copying that slice.

## Architecture

```
ui/            Compose screens + ViewModels (MVVM), grouped by feature
  theme/       Material 3 theme (one teal brand accent, light/dark)
  nav/         single NavHost; module routes
  dashboard/   clean home (KPIs + module grid) — replaces the 150-button grid
  policies/    REFERENCE SLICE: Screen + ViewModel + UiState
data/
  model/       DTOs mirroring the Go backend
  remote/      Retrofit ApiService + Network builder (BuildConfig.API_BASE_URL)
  repo/        repositories (single source of truth for the UI)
```

Each feature = `XxxScreen.kt` + `XxxViewModel.kt` (+ a repository + API method).
Copy `policies/` as the template.

## Reference slice (already working against dummy data)

`Policies`: `ApiService.listPolicies()` → `PolicyRepository` → `PoliciesViewModel`
→ `PoliciesScreen`. Run the Go backend, launch the app on an emulator, and the
list renders the backend's dummy policies.

## Clean information architecture

The original app exposed ~150 activities as a flat grid. The rebuild groups them
into **8 modules** reachable from the dashboard. Screens to build per module
(mapped from the original activities) are listed in `docs/REBUILD_TODO.md`.

| Module      | Covers (original screens, condensed)                              |
|-------------|-------------------------------------------------------------------|
| Clients     | client_list/profile/manage, family_*                              |
| Policies    | policy_list/details/status/tracker, add_policy, proposal, renewal |
| Leads       | prospect_*, leads_*, lead_extractor, recruitment, referrals       |
| Commission  | commission_*, due_report, latefee                                 |
| Reports     | business_report, cm_report*, summary, ranking                     |
| Agencies    | agency_* , agent_contact/import                                   |
| Marketing   | ai_* (poster/video), poster*, image*, invitation*, certificate*   |
| Tools       | pdf_*, documents, settings, notifications, data backup/download   |

## Not included (you implement under your LIC authorization)

Screens that drove LIC portals / read device SMS/notifications are **not**
rebuilt. They appear in the backend as the stubbed `licportal` integration
returning dummy data. See `docs/REBUILD_TODO.md` § "LIC portal integration".
