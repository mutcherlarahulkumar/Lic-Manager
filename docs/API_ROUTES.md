# Route-by-route reference (recovered via REA)

How the original app performs network calls, recovered by static analysis of
`BLISS Mobile Agent Pro.dll` (namespace `bma.*`) with REA
(`inspect-managed-members`: resolved `ldstr` literals + call edges per method).

The networking code splits into **two groups**, and this document treats them
very differently:

| Group | Classes | What it talks to | Documented here? |
|-------|---------|------------------|------------------|
| **A. Your backend** | `bma.HttpClass` (getURL/postURL/postImage/getimagebytes), `bma.DataUploadClass` | `bmaservices.in` (your PHP server) | **Yes — fully** |
| **B. LIC's system** | `bma.ApiClass` (Post/GetURL → `BFFService/...`), `bma.ApiService.FetchCookies`, `HttpClass.portalLogin*`, `AnandaSync`, `PolicyCheck*`, `PolicySync*` | LIC's internal **BFFService** / agent portals | **Boundary only — see §3** |

---

## 1. Shared transport (how any request is made)

All calls go through **OkHttp** (`OkHttpClient.NewCall` → `ICall.Execute`), with:

- A **cookie session** via Android's `CookieManager` / `CookieStore`. The app
  reads back Cloudflare cookies (`__cf_bm`, `_cfuvid`) — i.e. the servers sit
  behind Cloudflare.
- A **browser-like User-Agent** (desktop Chrome UA string) and `Connection:
  keep-alive`.
- Responses are returned to callers as a **string** and then parsed
  (JSON via `bma.JsonClass`, or HTML/delimited text).

`bma.ApiService.FetchCookies()` is the **session bootstrap**: it issues a GET to
establish the Cloudflare/session cookies before authenticated calls.

---

## 2. Group A — your `bmaservices.in` routes

These are issued by the generic `HttpClass` helpers and `DataUploadClass`. Verbs
are taken from the helper used and the URL shape; exact request bodies and
response JSON are noted where confirmed, else flagged for extraction (§4).

### `/ptdata` (core data API)
| Verb | Path | Params | Purpose | Caller |
|------|------|--------|---------|--------|
| GET  | `/ptdata/api.php` | `p=MERCHANT&v=…` | multiplexed op (`p` = operation) | HttpClass.getURL |
| GET  | `/ptdata/apinew.php` | `p=DO` | newer multiplexed op | HttpClass.getURL |
| GET  | `/ptdata/agentdata.php` | `token=` | agent data by session token | HttpClass.getURL |
| GET  | `/ptdata/jsonget.php` | `token=` | JSON blob by token | HttpClass.getURL |
| GET  | `/ptdata/getm.php` / `getm1.php` | `l=` | master data | HttpClass.getURL |
| GET  | `/ptdata/getmnew.php` | `key=` | master data (newer) | HttpClass.getURL |
| POST | `/ptdata/adddetails.php` | `u=` | add records for user | HttpClass.postURL |
| POST | `/ptdata/addjson.php` | `u=` | push JSON payload for user | HttpClass.postURL |
| POST | `/ptdata/adduser.php` / `adduserpass.php` | `l=` | create user / set credential | HttpClass.postURL |
| GET  | `/ptdata/insuremax.php` | `m=` | InsureMax read | HttpClass.getURL |
| POST | `/ptdata/insuremax_add.php` | — | InsureMax create | HttpClass.postURL |

### `/cloud` (backup/sync) — `DataUploadClass`
| Verb | Path | Purpose |
|------|------|---------|
| POST | `/cloud/uploaddata.php` | upload **zipped** local DB (`DataUploadClass.ZipFile` → upload); also an FTP path (`ftpUploadData`/`newFtpUpload`) |
| GET  | `/cloud/datadownload.php` | download data |

### `www.bmaservices.in/proposal`
| Verb | Path | Params | Purpose |
|------|------|--------|---------|
| POST | `/proposal/api_create.php` | — | create proposal |
| GET  | `/proposal/api_fetch.php` | — | fetch proposals |
| GET  | `/proposal/proposal_data.php` | `profile=` | proposal data by profile |

### `finserve.bmaservices.in`
| Verb | Path | Params | Purpose |
|------|------|--------|---------|
| GET  | `/api/getNotifications.php` | — | in-app notifications |
| GET  | `/ostatus.php` | `token=` | online status by token |
| POST | `/addstatus.php` | — | push status record |
| POST | `/manage/crashlog.php` | — | crash reporting |
| POST | `/manage/update_db.php` | `db=` | server-side DB update |

### `push.bmaservices.in` — `bma.BlissPushClient`
| Verb | Path | Purpose |
|------|------|---------|
| POST | `/api/` | push register/send (map to FCM in rebuild) |

### `/dompdf`
| Verb | Path | Purpose |
|------|------|---------|
| GET | `/dompdf/post.php?page=portrait` | server-side PDF generation |

**Known bugs to fix in the rebuild:** one call used host `bmaservices.in.in`
(double `.in`); one used plain `http://`. Use a single HTTPS host.

**Rebuild mapping:** replace the stringly-typed `api.php?p=…` multiplexing with
one typed REST route per operation under `/api/v1`, backed by Postgres. The Go
backend already stubs the list endpoints; fill request/response per §4.

---

## 3. Group B — LIC BFFService / portals (boundary only, deliberately not detailed)

REA shows `bma.ApiClass` and the portal methods talk to **LIC's own internal
systems** — e.g. `ApiClass`'s state machine references a `BFFService/prelogin/…`
path and an `Authorization` / `X-Correlation-Id` header scheme, and
`FetchCookies` establishes a browser-like authenticated session. The classes
involved: `ApiClass` (BFFService), `HttpClass.portalLogin/portalLoginServices/
logoutPortal/ShowOTPDialog`, `ApiService.FetchCookies`, `AnandaSync`,
`PolicyCheck.*`, `PolicySync.*`, and the cookie web clients.

**I have intentionally NOT documented the replication recipe for these** — i.e.
the exact endpoint sequence, auth/session handshake, header values and OTP flow
needed to drive LIC's BFFService/portals from a non-LIC client. That is
programmatic access to a third party's authenticated systems, which is outside
what this rebuild reproduces and must be implemented by you under your own LIC
authorization (ideally against an official LIC API).

In the rebuild these live behind the stubbed `backend/internal/licportal`
interface (dummy data today). See `docs/APPROACH.md` §3.

> This section is the honest "what was not produced, and why" that completes the
> route documentation: the mechanism was recoverable, but reproducing LIC access
> is the one thing this project leaves to you and your LIC agreement.

---

## 4. What's confirmed vs. still to extract

**Confirmed by REA:** the transport (OkHttp + cookie session + Cloudflare), the
endpoint URLs and query params above, which client method issues each kind of
call, and that responses come back as strings parsed by `JsonClass`.

**Not yet pinned (per endpoint):** exact POST field names and response JSON key
sets. The request methods are `async`, so those literals live in the
compiler-generated state machines (`bma.HttpClass/<postURL>d__N`,
`<getURL>d__N`, etc.), not the outer method. To extract them for a given route:

```bash
. scratchpad/rea-env.sh   # (your local REA env)
rea inspect-managed-members "BLISS Mobile Agent Pro.dll" --type '<postURL>d__N' --format json
#   -> methods[].body.anchors with operand_kind "string", resolved against
#      normalized_result.user_strings.literals (token -> value), give the exact
#      field names, headers and JSON keys that method uses.
```

Fill the request/response columns in `docs/API_SURFACE.md` as you extract each
one. Do this only for the Group A (bmaservices.in) routes.
