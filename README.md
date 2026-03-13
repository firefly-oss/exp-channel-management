# exp-channel-management

> Backend-for-Frontend service for channel initialisation, branding, and reference master data

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Module Structure](#module-structure)
- [Functional Verticals](#functional-verticals)
- [API Endpoints](#api-endpoints)
- [Domain SDK Dependencies](#domain-sdk-dependencies)
- [Configuration](#configuration)
- [Running Locally](#running-locally)
- [Testing](#testing)

## Overview

`exp-channel-management` is the experience-layer service responsible for bootstrapping digital channels (web, mobile, third-party apps) with the data they need before the user begins any journey. It aggregates branding configurations, available languages, and reference master data (countries, currencies, document types) into a single, channel-optimised API.

The service acts as a read-only aggregator. On channel start-up, the frontend can call a single `/init` endpoint to receive all bootstrapping data in one network round-trip, or call the individual endpoints when only a specific subset is needed. There is no persistent state or user session in this service.

All endpoints use **simple composition**: each request fans out to one or more downstream domain SDK calls in parallel, maps the results to journey-specific DTOs, and returns the aggregated response. No `@Workflow` or Redis is used.

## Architecture

```
Frontend / Mobile Channel
         |
         v
exp-channel-management  (port 8100)
         |
         +---> core-common-config-mgmt-sdk    (TenantBrandingsApi)
         |
         +---> domain-distributor-branding-sdk (BrandingApi)
         |
         +---> core-reference-master-data-sdk  (MasterDataApi)
```

The `/init` endpoint uses `Mono.zip()` to call all three downstream services simultaneously, minimising total latency on channel start-up.

## Module Structure

| Module | Purpose |
|--------|---------|
| `exp-channel-management-interfaces` | Shared DTO package (currently empty — DTOs live in `-core`) |
| `exp-channel-management-core` | Service interface, service implementation, query DTOs (`ChannelInitDTO`, `BrandingDTO`, `LanguageDTO`, `MasterDataDTO`) |
| `exp-channel-management-infra` | `ClientFactory` beans and `@ConfigurationProperties` for each downstream service |
| `exp-channel-management-web` | `ChannelManagementController`, Spring Boot application class, `application.yaml` |
| `exp-channel-management-sdk` | Auto-generated reactive SDK from the OpenAPI spec |

## Functional Verticals

| Vertical | Endpoints | Description |
|----------|-----------|-------------|
| Channel Init | 1 | Aggregated bootstrap call combining languages, branding, and master data |
| Languages | 2 | List all available locales; retrieve a single locale by ID |
| Branding | 1 | Active visual branding configuration (logo, colours, theme) |
| Master Data | 1 | Reference data required for form rendering (countries, currencies, document types) |

## API Endpoints

| Method | Path | Description | Response Code |
|--------|------|-------------|---------------|
| `GET` | `/api/v1/experience/channel/init` | Full channel initialisation — returns languages, active branding, and all reference master data in a single call | `200 OK` |
| `GET` | `/api/v1/experience/channel/languages` | List all available language/locale entries for this channel | `200 OK` |
| `GET` | `/api/v1/experience/channel/languages/{localeId}` | Retrieve a single language entry by its locale identifier | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/experience/channel/branding` | Retrieve the active visual branding configuration | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/experience/channel/master-data` | Retrieve all reference data (countries, currencies, document types) | `200 OK`, `404 Not Found` |

## Domain SDK Dependencies

| SDK | ClientFactory | APIs Used | Purpose |
|-----|--------------|-----------|---------|
| `core-common-config-mgmt-sdk` | `CoreConfigurationClientFactory` | `TenantBrandingsApi` | Tenant visual branding (logo, colours, theme) |
| `domain-distributor-branding-sdk` | `DistributorBrandingClientFactory` | `BrandingApi` | Distributor-level branding configuration |
| `core-reference-master-data-sdk` | `ReferenceMasterDataClientFactory` | `MasterDataApi`, `LanguageApi` | Countries, currencies, document types, language locales |

## Configuration

```yaml
server:
  port: ${SERVER_PORT:8100}

api-configuration:
  core-platform:
    reference-master-data:
      base-path: ${ENDPOINT_CORE_REFERENCE_MASTER_DATA:http://localhost:8060}
  domain-platform:
    core-configuration:
      base-path: ${ENDPOINT_DOMAIN_CORE_CONFIGURATION:http://localhost:8080}
    distributor-branding:
      base-path: ${ENDPOINT_DOMAIN_DISTRIBUTOR_BRANDING:http://localhost:8050}
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `8100` | HTTP server port |
| `SERVER_ADDRESS` | `localhost` | Bind address |
| `ENDPOINT_CORE_REFERENCE_MASTER_DATA` | `http://localhost:8060` | Base URL for `core-reference-master-data` |
| `ENDPOINT_DOMAIN_CORE_CONFIGURATION` | `http://localhost:8080` | Base URL for `core-common-config-mgmt` |
| `ENDPOINT_DOMAIN_DISTRIBUTOR_BRANDING` | `http://localhost:8050` | Base URL for `domain-distributor-branding` |

## Running Locally

```bash
# Prerequisites — ensure downstream services are running or their SDKs are installed locally
cd exp-channel-management
mvn spring-boot:run -pl exp-channel-management-web
```

Server starts on port `8100`. Swagger UI: [http://localhost:8100/swagger-ui.html](http://localhost:8100/swagger-ui.html)

## Testing

```bash
mvn clean verify
```

Tests cover `ChannelManagementServiceImpl` (unit tests with mocked SDK clients) and `ChannelManagementController` (WebTestClient-based controller tests verifying HTTP status codes and response shapes).
