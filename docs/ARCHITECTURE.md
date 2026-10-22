# BMA rebuild — architecture

A clean re-implementation of **BLISS Mobile Agent Pro** (`com.bliss.blissbma.pro`,
originally .NET MAUI / C#) on a modern, maintainable stack.

## Stack

| Layer     | Original                         | Rebuild                               |
|-----------|----------------------------------|---------------------------------------|
| Mobile    | .NET MAUI (C#), 150 activities   | **Kotlin + Jetpack Compose**, MVVM    |
| Backend   | `bmaservices.in` (your server)   | **Go** (chi router) — `backend/`      |
| Database  | bundled SQLite (`*.s3db`)        | **PostgreSQL** — `db/migrations/`     |
| Portals   | device-side LIC portal automation| **your** `licportal` impl (stubbed)   |

## How the pieces fit

```
Kotlin app  ──HTTPS──▶  Go backend  ──▶  PostgreSQL
                              │
                              └──▶  licportal.Client  (STUB → your impl)
                                        │
                                        └─▶  LIC portals / official API (yours)
```

- The app only ever talks to the Go backend. It never contacts LIC directly.
- The backend owns all data (Postgres) and is the only place the portal
  integration lives — isolated behind the `licportal.Client` interface.
- Everything runs **today** on dummy data: `store.DummyStore` +
  `licportal.StubClient`. Swap each for a real implementation independently.

## What was recovered from the APK (and how)

- **Framework**: .NET MAUI — `mono.MonoRuntimeProvider`, `libmonosgen-2.0.so`,
  `crc64…` Android-callable-wrapper class names.
- **Data model**: bundled SQLite DBs (`agcon.s3db` agent master, `licmdb.s3db`
  commission, `bmaextra.s3db` bonus rates) → `db/migrations/0001_init.sql`.
- **Feature set**: 150 app activities grouped into 8 modules (see
  `REBUILD_TODO.md`).
- **Own backend host**: `bmaservices.in`.

The app's business logic sits in 308 managed assemblies packed in
`lib/<abi>/libassemblies.*.blob.so` (an ELF-wrapped .NET AssemblyStore, LZ4
`XALZ` entries). Decompiling those to recover exact `bmaservices.in` request
shapes is listed as a follow-up task — see `REBUILD_TODO.md § Backend API`.

## Boundary / scope

The LIC-portal automation and device SMS/notification reading are **out of
scope** for this rebuild and are represented only as the stubbed `licportal`
interface. Implement them yourself under your LIC authorization, ideally against
an official LIC API.
