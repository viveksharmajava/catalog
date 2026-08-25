# Cloud Run / Docker env vars (prod profile)

Activate the prod profile on **every** Java service:

```text
SPRING_PROFILES_ACTIVE=prod
```

Cloud Run also injects `PORT` (usually `8080`); the prod stubs bind `server.port=${PORT:8080}`.

## Deployed backend URLs (asia-south1)

| Service | Cloud Run URL |
|---------|----------------|
| pricing | `https://pricing-1089274910156.asia-south1.run.app` |
| catalog | `https://catalog-1089274910156.asia-south1.run.app` |
| party | `https://party-service-1089274910156.asia-south1.run.app` |
| orders | `https://orders-service-1089274910156.asia-south1.run.app` |
| facility | `https://facility-service-1089274910156.asia-south1.run.app` |

These are the defaults in each `application-prod.properties`. Override with env vars only if a URL changes.

## Shared (all services that expose browser APIs)

| Env var | Example |
|---------|---------|
| `CORS_ALLOWED_ORIGINS` | `https://ecart-….run.app,https://catalog-admin-….run.app` |

Set this after the UI services are deployed (required for browser calls).

## catalog

| Env var | Default / example |
|---------|-------------------|
| `CATALOG_PUBLIC_BASE_URL` | `https://catalog-1089274910156.asia-south1.run.app` |
| `PARTY_SERVICE_BASE_URL` | `https://party-service-1089274910156.asia-south1.run.app` |
| `PRICING_SERVICE_BASE_URL` | `https://pricing-1089274910156.asia-south1.run.app` |
| `CORS_ALLOWED_ORIGINS` | (see shared) |

## orders

| Env var | Default / example |
|---------|-------------------|
| `FACILITY_SERVICE_BASE_URL` | `https://facility-service-1089274910156.asia-south1.run.app` |
| `CORS_ALLOWED_ORIGINS` | (see shared) |

## facility

| Env var | Default / example |
|---------|-------------------|
| `ORDERS_SERVICE_BASE_URL` | `https://orders-service-1089274910156.asia-south1.run.app` |
| `CORS_ALLOWED_ORIGINS` | (see shared) |

## party

| Env var | Example |
|---------|---------|
| `CORS_ALLOWED_ORIGINS` | (see shared) |
| `PASSWORD_RESET_BASE_URL` | `https://<ecart-host>/reset-password` |
| `PASSWORD_RESET_EXPOSE_LINK` | `false` |
| `GOOGLE_CLIENT_ID` | Web client ID (default already set in prod file) |
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

- `CATALOG_API_BASE` / `CATALOG_PROXY_TARGET` = `https://catalog-1089274910156.asia-south1.run.app`
- `PRICING_API_BASE` / `PRICING_PROXY_TARGET` = `https://pricing-1089274910156.asia-south1.run.app`
- `PARTY_API_BASE` / `PARTY_PROXY_TARGET` = `https://party-service-1089274910156.asia-south1.run.app`
- `ORDERS_API_BASE` / `ORDERS_PROXY_TARGET` = `https://orders-service-1089274910156.asia-south1.run.app`
- `FACILITY_API_BASE` / `FACILITY_PROXY_TARGET` = `https://facility-service-1089274910156.asia-south1.run.app`
- `NEXT_PUBLIC_CATALOG_IMAGE_BASE` = catalog URL above
- `NEXT_PUBLIC_GOOGLE_CLIENT_ID` = same as `GOOGLE_CLIENT_ID`

**catalog-admin** nginx env (same backend URLs as above):

- `CATALOG_SERVICE_BASE_URL`, `PRICING_SERVICE_BASE_URL`, `PARTY_SERVICE_BASE_URL`, `ORDERS_SERVICE_BASE_URL`, `FACILITY_SERVICE_BASE_URL`

**dealers** Android: use the HTTPS Cloud Run URLs (not `10.0.2.2`).

## Optional later (all services)

When moving off H2 to Cloud SQL, uncomment the datasource block in each `application-prod.properties` and set:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
