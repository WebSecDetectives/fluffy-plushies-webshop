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
| `GET /users` | No | No | No | Yes | Admin can list all users |
| `GET /users/{id}` | No | Own only | Own only | Yes | Users can view themselves |
| `PATCH /users/{id}` | No | Own only | Own only | Yes | Users can update themselves |
| `PATCH /users/{id}/password` | No | Own only | Own only | Yes | Users can update own password |
| `DELETE /users/{id}` | No | Own only | Own only | Yes | Users can delete themselves |
| `GET /auth/validate` | No | Yes | Yes | Yes | Token validation, if implemented |

## Inventory Service

| Endpoint | Everyone | USER | MERCHANT | ADMIN | Notes |
|---|---:|---:|---:|---:|---|
| `GET /items` | Public only | Public only | Public + own private | All | List visible products |
| `GET /items/{id}` | Public only | Public only | Public + own private | All | Private products require ownership/admin |
| `GET /items/mine` | No | No | Own only | All | Merchant product management |
| `POST /items` | No | No | Yes | Yes | Create product |
| `PATCH /items/{id}` | No | No | Own only | Yes | Edit product |
| `DELETE /items/{id}` | No | No | Own only | Yes | Delete product |
| `GET /reviews/item/{itemId}` | If item visible | If item visible | If item visible | Yes | Reviews follow item visibility |
| `POST /reviews/item/{itemId}` | No | If item visible | If item visible | Yes | Add review/comment |
| `GET /reviews` | No | No | No | Yes | Admin review moderation |
| `GET /reviews/{id}` | No | No | No | Yes | Admin review moderation |
