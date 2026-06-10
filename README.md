# KEA 2026 Security for Web Developers - Fluffy Plushies Webshop

[![Security Scans](https://github.com/WebSecDetectives/fluffy-plushies-webshop-backend/actions/workflows/security-scans.yml/badge.svg)](https://github.com/WebSecDetectives/fluffy-plushies-webshop-backend/actions/workflows/security-scans.yml)

## How to run the project

The application and its dependencies are dockerized, so no local database or service installation is needed.

1. Create a local `.env` file in the project root, using `dotenv-template` as a reference, and fill in the values (including the demo account passwords).
2. Start the system with Docker Compose from the project root:

   ```bash
   docker compose up -d
   ```

3. Open the app at <http://localhost>.

To do a clean restart including a database reset:

```bash
docker compose down -v
docker compose up --build -d
```

### Live demo

A hosted instance is available at <https://shop.starlords.eu/>. Note: it is online for a limited time only and may be taken down after evaluation.

### Services

| Service | URL |
| --- | --- |
| Webshop (frontend) | <http://localhost> |
| IdentityService | <http://localhost:8081/identity> |
| InventoryService | <http://localhost:8082/inventory> |

### Demo accounts

The `demo` Spring profile is enabled by default in Docker Compose and seeds three accounts. Log in with these usernames and the passwords you set in `.env`:

| Username | Role | Password (`.env`) |
| --- | --- | --- |
| `admin` | ADMIN | `IDENTITY_ADMIN_PASSWORD` |
| `merchant` | MERCHANT | `IDENTITY_MERCHANT_PASSWORD` |
| `user` | USER | `IDENTITY_USER_PASSWORD` |

## Environment

See `dotenv-template` in the project root for the required environment variables.

## API testing with Postman

A Postman collection is provided at `Postman/Fluffy-Plushies-Webshop.postman_collection.json`. It covers authentication, registration, role-based access control, item and review CRUD, image upload, and SQL/command injection checks.

1. Import the collection into Postman.
2. The password variables are committed blank for security. Set these three collection variables to the same values used in `.env`:

   | Collection variable | Set to (`.env`) |
   | --- | --- |
   | `adminPassword` | `IDENTITY_ADMIN_PASSWORD` |
   | `merchantPassword` | `IDENTITY_MERCHANT_PASSWORD` |
   | `userPassword` | `IDENTITY_USER_PASSWORD` |

3. With the stack running locally, run the collection top to bottom (or use the Collection Runner). The login requests set the auth tokens automatically, so later requests are authorized. The base URLs default to the local services (`localhost:8081` and `localhost:8082`).