# bmaservices.in — API surface & client contract

Recovered from your own app assembly `BLISS Mobile Agent Pro.dll` (namespace
`bma.*`) via REA static analysis. This is the integration surface the Go backend
reproduces. It is enough to code against cleanly.

> Scope: external **LIC portal** calls (`bma.HttpClass.portalLogin*`,
> `bma.AnandaSync`, `bma.PolicyCheck.*`, `bma.PolicySync.*`, cookie web clients,
> notification listener) are **out of scope** and stay behind the stubbed
> `licportal` package. Implement them yourself under your LIC authorization.

## How the app talks to your backend

Two thin HTTP helper classes do all the work; features call these with a URL and
params. The response is always returned as a **string** (the server replies with
JSON or a delimited text payload that the app then parses).

| Class.method (recovered signature)                     | Role                                   |
|--------------------------------------------------------|----------------------------------------|
| `ApiClass.GetURL(string, string, string) → string`     | GET to `/ptdata/*.php` style endpoints |
| `ApiClass.Post(string, string, string) → string`       | POST with body to `/ptdata/*.php`      |
| `HttpClass.getURL(url, arg, bool) → string`             | generic GET                            |
| `HttpClass.getURLSimple(url, arg) → string`             | generic GET (no extras)                |
| `HttpClass.postURL(url, body, arg, bool) → string`      | generic POST                           |
| `HttpClass.postImage(url, …) → byte[]`                  | multipart image upload / fetch         |
| `HttpClass.getimagebytes(url, arg, bool) → byte[]`      | binary (image/pdf) download            |
| `ApiService.FetchCookies() → void`                      | session cookie bootstrap (auth state)  |
| `DataUploadClass.UploadAgencyData / ftpUploadData / newFtpUpload / ZipFile` | backup upload (HTTP + FTP), zips local DB first |

**Implication for the rebuild:** the original uses a multiplexed, stringly-typed
API (`api.php?p=MERCHANT&v=…` returning a string). The Go backend replaces this
with typed REST + JSON (one route per operation). Keep a session/token instead
of raw cookies.

## Endpoints (host → path → purpose)

Hosts: `bmaservices.in`, `www.bmaservices.in`, `finserve.bmaservices.in`,
`push.bmaservices.in`.

### `/ptdata` — core data API (via `ApiClass`)
| Method | Path | Query params | Purpose |
|--------|------|--------------|---------|
| GET  | `/ptdata/api.php`        | `p=MERCHANT&v=…` | multiplexed op; `p` selects operation |
| GET  | `/ptdata/apinew.php`     | `p=DO`           | newer multiplexed op (DO = development officer) |
| GET  | `/ptdata/agentdata.php`  | `token=`         | fetch agent data by session token |
| GET  | `/ptdata/jsonget.php`    | `token=`         | fetch JSON blob by token |
| GET  | `/ptdata/getm.php` / `getm1.php` | `l=`     | fetch master data |
| GET  | `/ptdata/getmnew.php`    | `key=`           | fetch master data (newer) |
| POST | `/ptdata/adddetails.php` | `u=`             | add record(s) for user |
| POST | `/ptdata/addjson.php`    | `u=`             | push JSON payload for user |
| POST | `/ptdata/adduser.php` / `adduserpass.php` | `l=` | create user / set credential |
| GET  | `/ptdata/insuremax.php`  | `m=`             | InsureMax module read |
| POST | `/ptdata/insuremax_add.php` | —             | InsureMax create |

### `/cloud` — backup/sync (via `DataUploadClass`)
| Method | Path | Purpose |
|--------|------|---------|
| POST | `/cloud/uploaddata.php`   | upload zipped local DB (backup) |
| GET  | `/cloud/datadownload.php` | download data |

> ⚠️ Bug in original: a call used host `bmaservices.in.in` (double `.in`) and one
> used plain `http://`. Fix both in the rebuild (single host, HTTPS only).

### `www.bmaservices.in/proposal`
| Method | Path | Query | Purpose |
|--------|------|-------|---------|
| POST | `/proposal/api_create.php`   | —          | create a proposal |
| GET  | `/proposal/api_fetch.php`    | —          | fetch proposals |
| GET  | `/proposal/proposal_data.php`| `profile=` | proposal data by profile |

### `finserve.bmaservices.in`
| Method | Path | Query | Purpose |
|--------|------|-------|---------|
| GET  | `/api/getNotifications.php` | —        | in-app notifications |
| GET  | `/ostatus.php`              | `token=` | online status by token |
| POST | `/addstatus.php`            | —        | push a status record |
| POST | `/manage/crashlog.php`      | —        | crash reporting |
| POST | `/manage/update_db.php`     | `db=`    | server-side DB update |
| GET  | `/`, `/admin`               | `agcode=`| agency dashboards (web) |

### `push.bmaservices.in` (via `bma.BlissPushClient`)
| Method | Path | Purpose |
|--------|------|---------|
| POST | `/api/` | push notification register/send → map to FCM in rebuild |

### `/dompdf`
| GET | `/dompdf/post.php?page=portrait` | server-side PDF generation |

## Per-endpoint documentation template (fill as you decompile)

For each endpoint above, capture the exact request/response by decompiling its
caller, then record it here in this shape:

```
### POST /ptdata/addjson.php?u={userId}
What: uploads a batch of <entity> records for the given user.
Caller: bma.<Type>.<Method>
Auth: session cookie from ApiService.FetchCookies  (rebuild: Bearer token)
Request (form/body): { field: type, ... }
Response (string → JSON): { field: type, ... }  | or pipe/CSV-delimited: col1|col2|...
Errors: <observed error markers, e.g. "ERROR", empty body>
Rebuild route: POST /api/v1/<resource>
```

How to get the exact bodies with REA (per type):

```
. scratchpad/rea-env.sh
rea inspect-managed-members "<BLISS Mobile Agent Pro.dll>" --type bma.ApiClass --type bma.HttpClass --format json
#   -> methods[].body.anchors show the ldstr/call sequence; resolve ldstr tokens
#      to recover literal URLs, form-field names and JSON keys per method.
```

## Status

- [x] Hosts, endpoints, methods, query params, client architecture — recovered.
- [ ] Exact request bodies + response JSON per endpoint — fill via decompile (task #3).
