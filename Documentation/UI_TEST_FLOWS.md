# UI Test Flows

Manual frontend test flows, run against the Docker backend (`localhost:4200` dev server).
Add new flows as features land; re-run relevant flows after dependency or auth changes.

Seeded accounts: `admin`, `merchant`, `user` (passwords in `.env`).
Seed data: 1 PRIVATE item ("secret prototype plushie", owned by `merchant`) + 10 PUBLIC items with reviews.

## Auth / session

- [ ] Login (`/login`) as any seeded account → header shows `username (ROLE)`
- [ ] Wrong password → generic "Login failed" (no username-exists hint)
- [ ] Logout → header switches to Sign up/Login
- [ ] Login → refresh page → still logged in (token persists)
- [ ] Logout → refresh page → still logged out
- [ ] DevTools → Network: requests to Identity/Inventory carry `Authorization: Bearer`; third-party requests (e.g. Firebase) do not

## Registration (`/create`)

- [ ] Submit empty form → per-field messages appear (no alert)
- [ ] Username < 3 or > 30 chars → length message
- [ ] Weak password → policy message (12–100, upper/lower/digit/special, no spaces)
- [ ] Invalid email → format message
- [ ] Valid form → "Registration successful", can log in with new account
- [ ] Network tab: request body is snake_case (`postal_code`, `img_url`) — wire-format interceptor working

## Product visibility (core RBAC demo)

- [ ] Logged out → home shows **10** items (PUBLIC only)
- [ ] Login `merchant` → **11** items, secret plushie first
- [ ] Login `user` → **10** items (logged in ≠ owner)
- [ ] Login `admin` → **11** items (admin sees everything)
- [ ] Logout while on home → private item disappears immediately (no manual refresh needed)
- [ ] Network tab evidence: same `GET /items`, different result per token

## Item detail (`/items/:id`)

- [ ] Click a card on home → detail renders (name, price, details table, image with fallback)
- [ ] Private item URL as merchant/admin → renders with PRIVATE badge
- [ ] Same URL logged out / as `user` → generic "Product not found" (existence-hiding 404)
- [ ] Nonsense UUID in URL → same generic "Product not found"

## Merchant flows

- [ ] As `merchant`: header shows "My products" link; as `user`/`admin`/logged-out it doesn't
- [ ] `/my-products` → own items listed (PRIVATE badge where relevant)
- [ ] As `user`: typing `/my-products` in the URL redirects home (roleGuard)
- [ ] Backend check: `GET /items/mine` with a `user` token (Postman/curl) → 403 (server-side enforcement, not just UI)
- [ ] Create product (`/my-products/new`): empty submit → per-field messages
- [ ] Create PUBLIC product → lands on detail page, visible on home logged-out
- [ ] Create PRIVATE product → visible to owner/admin only

## Item edit/delete (item detail)

- [ ] As `merchant` on own item → Edit + Delete buttons visible; on another merchant's item → hidden
- [ ] As `admin` → Edit + Delete on every item
- [ ] As `user`/logged out → no Edit/Delete buttons
- [ ] Edit → form prefilled → save → detail shows updated values
- [ ] Delete → confirm → item gone from home list
- [ ] Backend check: `PATCH`/`DELETE /items/{id}` with a `user` token → 403; with another merchant's token → 404/403 (server-side ownership)

## Admin: create merchant (`/admin/create-merchant`)

- [ ] As `admin`: "New merchant" header link visible; form submits → success message
- [ ] New merchant can log in, sees "My products" (empty), can create products
- [ ] As `merchant`/`user`: no header link; URL redirects home (roleGuard)
- [ ] Backend check: `POST /users/merchant` with non-admin token → 403

## Reviews (item detail)

- [ ] Logged out → reviews visible on public items, "Log in to write a review"
- [ ] As `user` → submit review → appears in list; delete button only on own reviews
- [ ] As `merchant` → no review form (role may not review)
- [ ] As `admin` → delete button on every review
- [ ] Delete own review → disappears from list

## Image upload & rendering

- [ ] Create product as `merchant` with a JPEG selected → detail page shows the uploaded image
- [ ] Create product without an image → cards/detail show the fallback plushie
- [ ] Edit product: current image shown as thumbnail above the file input
- [ ] Edit product: choose a new PNG (thumbnail switches to preview) → save → image replaced
- [ ] Edit product: leave file empty → save → existing image unchanged
- [ ] Pick a `.txt` or >2MB file → rejected client-side with a message, no request sent
- [ ] Rename a `.txt` to `.png` → rejected client-side by the magic-byte check ("file content is not a valid…")
- [ ] Bypass the client entirely (Postman `POST /inventory/items/{id}/image` with `NOT_AN_IMG.jpg`) → backend 400
- [ ] Server-side rejection through the form (e.g. >4096px JPEG under 2MB) → product saved, dismissible "image could not be uploaded" banner shown on the item page (✕ closes it)
- [ ] PRIVATE item image: renders for owner `merchant` and `admin`; logged out/`user` → fallback (GET 404)
- [ ] DevTools Network: `GET /inventory/items/{id}/image` carries `Authorization: Bearer`, returns `image/png`
- [ ] Scroll the home list and navigate around → no growing blob memory (object URLs revoked)

## Security spot-checks

- [ ] Stored XSS: submit `<img src=x onerror=alert(document.domain)>` as review text →
      renders as literal text, no dialog (full plan in `REPORT_NOTES.md` "XSS defence")
- [ ] Same payload as username at registration → header shows literal text
- [ ] Tampered JWT in localStorage (edit a character) → app treats you as logged out / backend 401s
- [ ] CSRF PoC: while logged in, open a local `csrf-poc.html` that auto-submits
      `POST /inventory/items` → backend 401 (no cookie to ride, header not attached by attacker page);
      screenshot for report (see REPORT_NOTES "Session storage & CSRF")