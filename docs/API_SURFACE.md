# bmaservices.in — recovered API surface

Endpoints the original app calls on **your own backend** (`bmaservices.in` and
its subdomains), recovered from the app assembly `BLISS Mobile Agent Pro.dll`
(string literals in the .NET metadata). This is the integration surface the Go
backend should reproduce/proxy during migration.

> Scope note: the app also calls external LIC portals (e.g. a `/LICEPS/Login/…`
> path was seen). Those are **not** documented here and remain behind the
> stubbed `licportal` package — implement under your LIC authorization.

## Hosts

| Host                       | Purpose (inferred)                          |
|----------------------------|---------------------------------------------|
| `bmaservices.in`           | main app data API (`/ptdata`, `/cloud`)     |
| `www.bmaservices.in`       | proposal module (`/proposal`)               |
| `finserve.bmaservices.in`  | status / notifications / admin / uploads    |
| `push.bmaservices.in`      | push notification API                       |

## Endpoints (method + visible query params)

> Request bodies and response JSON shapes are built inside the calling methods;
> see "Next: exact bodies" below to finish capturing them per endpoint. Visible
> query parameters are listed as-is.

### `bmaservices.in/ptdata` — core data API
- `GET  /ptdata/api.php?p=MERCHANT&v=…` — multiplexed API; `p` selects the op
  (e.g. `MERCHANT`). Catalogue every `p` value (see TODO).
- `GET  /ptdata/apinew.php?p=DO` — newer multiplexed API (`p=DO` = development officer).
- `POST /ptdata/adddetails.php?u=…` — add record(s) for user `u`.
- `POST /ptdata/addjson.php?u=…` — push a JSON payload for user `u`.
- `POST /ptdata/adduser.php?l=…` / `adduserpass.php?l=…` — user/credential create.
- `GET  /ptdata/agentdata.php?token=…` — fetch agent data by session token.
- `GET  /ptdata/getm.php?l=…` / `getm1.php?l=…` / `getmnew.php?key=…` — fetch master data.
- `GET  /ptdata/jsonget.php?token=…` — fetch JSON blob by token.
- `GET  /ptdata/insuremax.php?m=…` / `POST /ptdata/insuremax_add.php` — InsureMax module.
- `GET  http://bmaservices.in/ptdata/api.php` — NOTE: plain HTTP seen; move to HTTPS.

### `bmaservices.in/cloud` — data backup/sync
- `POST /cloud/uploaddata.php` — upload local DB/data (backup).
- `GET  /cloud/datadownload.php` — download data.
- ⚠️ `https://bmaservices.in.in/cloud/datadownload.php` — **bug in original**
  (double `.in`); fix to a single host in the rebuild.

### `www.bmaservices.in/proposal` — proposals
- `POST /proposal/api_create.php` — create a proposal.
- `GET  /proposal/api_fetch.php` — fetch proposal(s).
- `GET  /proposal/proposal_data.php?profile=…` — proposal data by profile.

### `finserve.bmaservices.in` — status / notifications / admin
- `GET  /api/getNotifications.php` — in-app notifications.
- `GET  /ostatus.php?token=…` — order/online status by token.
- `POST /addstatus.php` — push a status record.
- `GET  /?agcode=…` and `/admin?agcode=…` — agency dashboards (web).
- `GET  /agdata/`, `/uploads/` — static/agency assets.
- `POST /manage/crashlog.php` — crash reporting.
- `POST /manage/update_db.php?db=…` — server-side DB update trigger.

### `push.bmaservices.in`
- `… /api/` — push notification send/register. Map to FCM in the rebuild.

### `/dompdf`
- `GET /dompdf/post.php?page=portrait` — server-side PDF generation.

## Mapping to the Go backend

Reproduce these as clean REST under `/api/v1`, backed by Postgres, and keep a
thin `bliss` client (`backend/internal/bliss`) for any endpoint you proxy to the
existing server during cut-over. The multiplexed `api.php?p=…` style should be
split into proper resource routes (see REBUILD_TODO.md § 1).

## Next: exact request/response bodies

For each endpoint, decompile the calling method to capture the exact POST fields
and the response JSON:

```
rea decompile <BLISS Mobile Agent Pro.dll> --<type/method selector>
# or: rea inspect-managed-members … to list the HTTP service type first
```

Tracked as task #3. Do this per endpoint and fill the request/response columns
in this file as you go.
