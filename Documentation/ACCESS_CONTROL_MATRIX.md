# Access Control Matrix

## Roles

- `Everyone`: unauthenticated visitors
- `USER`: normal customer account
- `MERCHANT`: seller account that can manage its own products
- `ADMIN`: administrator with full access

## Identity Service

| Endpoint | Everyone | USER | MERCHANT | ADMIN | Notes |
|---|---:|---:|---:|---:|---|
| `POST /auth/login` | Yes | Yes | Yes | Yes | Login endpoint |
| `POST /users` | Yes | Yes | Yes | Yes | Register normal user |
| `POST /users/merchant` | No | No | No | Yes | Admin creates merchant accounts |
| `POST /users/admin` | No | No | No | Yes | Admin creates admin accounts |
| `GET /users` | No | No | No | Yes | Admin can list all users |
| `GET /users/{id}` | No | Own only | Own only | Yes | Users can view themselves |
| `PATCH /users/{id}` | No | Own only | Own only | Yes | Users can update themselves |
| `PATCH /users/{id}/password` | No | Own only | Own only | Yes | Users can update own password |
| `DELETE /users/{id}` | No | Own only | Own only | Yes | Users can delete themselves |
| `GET /auth/validate` | No | Yes | Yes | Yes | Token validation, if implemented |

## Inventory Service

| Endpoint | Everyone | USER | MERCHANT | ADMIN | Notes |
|---|---:|---:|---:|---:|---|
| `GET /items` | Public only | Public only | Public + own private | All | List filtered to visible rows |
| `GET /items/{id}` | Public only | Public only | Public + own private | All | Private item hidden from others → **404** (not 403) |
| `GET /items/mine` | No | No | Own only | No | MERCHANT-only endpoint; admin uses `GET /items` (All) |
| `POST /items` | No | No | Yes | **No** | **MERCHANT only** — admin is not a superuser here (deliberate RBAC). `merchantId` set from JWT, never the body |
| `PATCH /items/{id}` | No | No | Own only | Yes | Not-owned private → **404** (hidden); not-owned public → **403** |
| `DELETE /items/{id}` | No | No | Own only | Yes | Same ownership rule as PATCH (404 hidden / 403 not-owned) |
| `GET /reviews/item/{itemId}` | If item visible | If item visible | If item visible | Yes | Reviews follow item visibility |
| `POST /reviews/item/{itemId}` | No | If item visible | No | Yes | USER/ADMIN only (merchants don't review); `reviewerId` from JWT |
| `GET /reviews` | No | No | No | Yes | Admin review moderation |
| `GET /reviews/{id}` | No | No | No | Yes | Admin review moderation |
| `PATCH /reviews/{id}` | No | Own only | No | Yes | Author edits own review; admin any (moderation) |
| `DELETE /reviews/{id}` | No | Own only | No | Yes | Author deletes own; admin any (moderation) |

## Enforcement & conventions

- **Where it's enforced:** role checks at the endpoint via `@PreAuthorize` (authorities
  come from the signature-verified JWT, surfaced as the `AuthUser` principal);
  *instance-level* ownership/visibility is enforced in the service layer, since it
  depends on the row's owner (`@PreAuthorize` can't express "is this mine?").
- **Ownership source:** an item's `merchantId` is the JWT subject, set server-side on
  create — never accepted from the request body.
- **404 vs 403 (information hiding):** a resource the caller may not even *see* (a private
  item that isn't theirs) returns **404**, so its existence isn't disclosed. **403** is
  used only when the resource is visible but the action isn't allowed (e.g. a merchant
  editing another merchant's *public* item). Both create and update use dedicated DTOs
  with no `merchantId`/`id` to prevent mass-assignment.
- See `REPORT_NOTES.md` for the rationale behind each of these choices.
