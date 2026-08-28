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

## Shared Cloud SQL MySQL (all backends)

All five services use the **same** Cloud SQL instance and database:

| Setting | Value |
|---------|-------|
| Instance ID | `playpro` |
| Connection name | `project-3e935033-4ac7-4904-bad:asia-south1:playpro` |
| Database / schema | `playdb` |
| DB user | `playpro_user` |

JDBC uses the Cloud SQL socket factory (no public IP required). Attach Cloud SQL instance **playpro** to every Cloud Run backend.

Because all services share `playdb`, each service uses a **separate Flyway history table**:

| Service | `spring.flyway.table` |
|---------|------------------------|
| catalog | `flyway_schema_history_catalog` |
| orders | `flyway_schema_history_orders` |
| party | `flyway_schema_history_party` |
| pricing | `flyway_schema_history_pricing` |
| facility | `flyway_schema_history_facility` |

### Env vars to set on **every** Cloud Run backend

| Env var | Value | Notes |
|---------|-------|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Required |
| `MYSQL_USER` | `playpro_user` | Already the default in prod properties |
| `MYSQL_PASSWORD` | *(your DB password)* | **Secret Manager** — required; do not commit |
| `MYSQL_DATABASE` | `playdb` | Already the default |
| `CLOUD_SQL_INSTANCE` | `project-3e935033-4ac7-4904-bad:asia-south1:playpro` | Already the default; override if project/region differs |

Optional overrides:

| Env var | Purpose |
|---------|---------|
| `SPRING_DATASOURCE_URL` | Full JDBC URL override |
| `SPRING_DATASOURCE_USERNAME` | Same as `MYSQL_USER` |
| `SPRING_DATASOURCE_PASSWORD` | Same as `MYSQL_PASSWORD` |
| `MYSQL_POOL_SIZE` | Hikari max pool (default `10`) |
| `MYSQL_POOL_MIN_IDLE` | Hikari min idle (default `2`) |

### Cloud Run checklist (each backend)

1. Set env vars above (at least `SPRING_PROFILES_ACTIVE=prod` and `MYSQL_PASSWORD`).
2. **Connections → Cloud SQL** → add instance `playpro` (connection name above).
3. Redeploy / restart so the socket factory can reach the instance.

Example `gcloud` (repeat for catalog, orders, party, pricing, facility):

```bash
gcloud run services update catalog \
  --region=asia-south1 \
  --add-cloudsql-instances=project-3e935033-4ac7-4904-bad:asia-south1:playpro \
  --set-env-vars=SPRING_PROFILES_ACTIVE=prod,MYSQL_USER=playpro_user,MYSQL_DATABASE=playdb \
  --set-secrets=MYSQL_PASSWORD=mysql-playpro-password:latest
```

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

## Note on Flyway / SQL dialect

Local profiles still use H2. Some Flyway scripts use H2/Postgres-oriented types (`CLOB`, `GENERATED BY DEFAULT AS IDENTITY`). If Flyway fails against MySQL, adapt those migrations (or add MySQL-specific scripts) before enabling prod traffic.
