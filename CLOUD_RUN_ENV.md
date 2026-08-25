# Cloud Run / Docker env vars (prod profile)

Activate the prod profile on **every** Java service:

```text
SPRING_PROFILES_ACTIVE=prod
```

Cloud Run also injects `PORT` (usually `8080`); the prod stubs bind `server.port=${PORT:8080}`.

Replace the example hosts with your real Cloud Run URLs (no trailing slash).

## Shared (all services that expose browser APIs)

| Env var | Example |
|---------|---------|
| `CORS_ALLOWED_ORIGINS` | `https://shop.example.com,https://admin.example.com` |

## catalog

| Env var | Example |
|---------|---------|
| `CATALOG_PUBLIC_BASE_URL` | `https://catalog-xxxxx.run.app` |
| `PARTY_SERVICE_BASE_URL` | `https://party-xxxxx.run.app` |
| `PRICING_SERVICE_BASE_URL` | `https://pricing-xxxxx.run.app` |
| `CORS_ALLOWED_ORIGINS` | (see shared) |

## orders

| Env var | Example |
|---------|---------|
| `FACILITY_SERVICE_BASE_URL` | `https://facility-xxxxx.run.app` |
| `CORS_ALLOWED_ORIGINS` | (see shared) |

## facility

| Env var | Example |
|---------|---------|
| `ORDERS_SERVICE_BASE_URL` | `https://orders-xxxxx.run.app` |
| `CORS_ALLOWED_ORIGINS` | (see shared) |

## party

| Env var | Example |
|---------|---------|
| `CORS_ALLOWED_ORIGINS` | (see shared) |
| `PASSWORD_RESET_BASE_URL` | `https://shop.example.com/reset-password` |
| `PASSWORD_RESET_EXPOSE_LINK` | `false` |
| `GOOGLE_CLIENT_ID` | your Google OAuth Web client ID |
| `PARTY_MAIL_ENABLED` | `true` |
| `PARTY_MAIL_FROM` | `noreply@example.com` |
| `SPRING_MAIL_HOST` | `smtp.gmail.com` |
| `SPRING_MAIL_PORT` | `587` |
| `SPRING_MAIL_USERNAME` | SMTP user |
| `SPRING_MAIL_PASSWORD` | **Secret Manager** — do not put in source |

## pricing

| Env var | Example |
|---------|---------|
| `CORS_ALLOWED_ORIGINS` | (see shared) |

## Frontends (not Spring, but required in GCP)

**ecart** (Cloud Run / env):

- `CATALOG_API_BASE`, `PRICING_API_BASE`, `PARTY_API_BASE`, `ORDERS_API_BASE`, `FACILITY_API_BASE`
- matching `*_PROXY_TARGET` if you still use Next rewrites
- `NEXT_PUBLIC_CATALOG_IMAGE_BASE` = public catalog URL
- `NEXT_PUBLIC_GOOGLE_CLIENT_ID` = same as `GOOGLE_CLIENT_ID`

**catalog-admin**: point its API proxy / Vite env at the catalog (and related) GCP URLs.

**dealers** Android: production `buildConfigField` / product flavor for `CATALOG_BASE_URL`, etc. (emulator `10.0.2.2` will not work against GCP from a real device — use HTTPS service URLs).

## Optional later (all services)

When moving off H2 to Cloud SQL, uncomment the datasource block in each `application-prod.properties` and set:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
