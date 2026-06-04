# KEA 2026 Security for Web Developers - Fluffy Plushies Webshop

[![Security Scans](https://github.com/WebSecDetectives/fluffy-plushies-webshop-backend/actions/workflows/security-scans.yml/badge.svg)](https://github.com/WebSecDetectives/fluffy-plushies-webshop-backend/actions/workflows/security-scans.yml)

## How to run the project

## Environment

See `dotenv-template` in the project root for required environment variables.

## Docker

The applications and their dependencies are dockerized, so no local database or service installation is needed. Create a local `.env` file using `dotenv-template` as reference, then start the system with Docker Compose from parent repo.

To start the system:
```bash
docker compose up -d
```

To do a clean restart including database reset:

```bash
docker compose down -v
docker compose up --build -d
```