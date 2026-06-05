# Dependency Security & Dependabot Remediation

Record of supply-chain / dependency vulnerability handling for the SWD exam report.
Tracks each Dependabot alert we address: vulnerable package, severity, fix, and verification.

## Scope

- **In scope (SWD):** `IdentityService`, `InventoryService` (backend), `Webshop` (Angular frontend).
- **Out of scope:** `OrderService` (.NET) and `EmailService` (Node/TS) are **not part
  of this project** and are excluded from the SWD evaluation. Their Dependabot alerts are recorded
  for completeness but are **not remediated** unless the project scope is explicitly changed.
- **Ownership:** all services belong to SirMeows (frontend taken over from M-K1234, June 2026).

## Triage method

- Pull open alerts from GitHub REST API `GET /repos/{owner}/{repo}/dependabot/alerts`
  (authenticated with a read-only PAT; token kept out of source control via `.gh_pat`/env var).
- Prefer fixing at the version-property level (Spring Boot BOM / centralized `*.version`
  properties, or a single framework bump) so one change resolves many transitive CVEs.
- Check current compatible versions before bumping; do not rely on remembered versions.
- Verify after each change: compile + tests where they exist, then a Postman smoke run.
- Keep the two backend services on identical versions for shared dependencies (no drift).

### InventoryService Spring upgrade (2026-06-01)

| # | Package | Severity | Vulnerable | Fix | GHSA | Verified |
|---|---------|----------|-----------|-----|------|----------|
| 79 | spring-security-web | 🔴 critical | 6.4.0–6.4.13 | Spring Security 6.4.4 → **7.0.5** (via Boot 4 alignment) | GHSA-mf92-479x-3373 | Build + docker compose + Postman green |
| 106 | spring-boot-devtools | 🟠 high | 3.4.0–3.4.15 | Spring Boot 3.4.4 → **4.0.6** | GHSA-56v8-86gj-66jp | Build + docker compose + Postman green |

Supporting version work that drives the above (one bump, many transitive CVEs pre-empted):
Spring Boot 3.4.4 → 4.0.6, Spring Security 6.4.4 → 7.0.5, mysql-connector-j 9.2.0 → 9.7.0,
h2 2.3.232 → 2.4.240, datafaker 2.4.2 → 2.5.1, jjwt 0.12.6 → 0.12.7, lombok 1.18.38 → 1.18.42,
modelmapper 3.2.2 → 3.2.6, uuid-creator 6.1.0 → 6.1.1, hibernate-validator 8.0.2 → 9.0.1.Final;
`spring-boot-starter-aop` → `-aspectj`; all versions centralized via POM properties. Also fixed
IdentityService's unused `hibernate.validator.version` property (was silently on 8.0.2.Final).

### IdentityService Spring upgrade (2026-05-29)

When SirMeows migrated `IdentityService` from Spring Boot 3.4 / Security 6.4 to Boot 4 / Security 7
a few days earlier, GitHub auto-closed exactly these alerts — the *same* critical + high pair that
`InventoryService` still carried until today (#79 / #106 above). One framework bump, no code change.

| # | Package | Severity | Vulnerable | Fixed by | GHSA |
|---|---------|----------|-----------|----------|------|
| 80 | spring-security-web | 🔴 critical | 6.4.0–6.4.13 | Spring Security → 7.0.5 (Boot 4 upgrade) | GHSA-mf92-479x-3373 |
| 107 | spring-boot-devtools | 🟠 high | 3.4.0–3.4.15 | Spring Boot → 4.0.6 | GHSA-56v8-86gj-66jp |

**Why the backend list is short (and why that's the point):** across both Spring services the
framework upgrade closed just **2 Dependabot alerts (1 critical + 1 high)** — versus **55** on the
npm frontend. The difference is dependency management style: Spring Boot's parent BOM keeps the
*transitive* dependency graph on patched versions, so few CVEs ever surface as alerts and a single
`spring-boot-starter-parent` bump clears the ones that do. The npm tree has no equivalent central
pin, so transitive CVEs accumulate (protobufjs ×9, tar ×6, lodash ×8, vite ×5…). The takeaway : *staying current on one managed framework version is a high-leverage, low-effort
supply-chain control.*

> Same-day housekeeping (not the framework bump itself): 2 critical GitHub Actions alerts for
> `aquasecurity/trivy-action` (CI workflows) were also closed on 2026-05-29 (→ 0.35.0).

---

## Frontend remediation log (Webshop)

Most of these are **transitive** dependencies. The high-leverage fixes:

1. Bump `@angular/*` to **≥ 20.3.18** (covers common/compiler/core alerts 34, 37, 42, 43, 67, 75, 76).
2. Bump `firebase` to latest (its tree pulls protobufjs, @protobufjs/utf8, fast-uri, follow-redirects, ip-address).
3. `npm update` + `npm audit fix` and commit the refreshed `package-lock.json` for the build-tooling
   transitives (vite, rollup, tar, glob, minimatch, picomatch, postcss, tmp, immutable, flatted, lodash, qs).
4. Re-run the Dependabot scan / `npm audit` and confirm the count drops; fill the Status column below.

### Applied remediation (2026-06-05)

Alert clusters as triaged from the live Dependabot API, and how each was cleared:

| Cluster | Worst severity | Fix applied |
|---|---|---|
| `protobufjs` (+`@protobufjs/utf8`) ×10 | 🔴 critical | transitive via `firebase` → cleared by `npm update` (firebase 12.0.0 → **12.14.0**, protobufjs → 7.6.2) |
| `lodash` ×9 (ranges as old as < 4.17.12) | 🔴 critical | ancient copy (3.10.1) came via the **unused `firestore@1.1.6` package** (2016-era, also bundled firebase 2.4.2) → **removed from package.json**; karma's copy → 4.18.1 via `npm update` |
| `@angular/core`/`compiler`/`common` ×7 | 🟠 high | `npm update` within `^20` → **20.3.24** (≥ 20.3.18 required) |
| `firebase` < 10.9.0 | 🟡 medium | alert matched the nested firebase 2.4.2 inside `firestore` → gone with its removal |
| Build chain: `vite`, `rollup`, `tar`, `glob`, `minimatch`, `picomatch`, `tmp`, `flatted`, `immutable`, `postcss`, `qs`, `fast-uri`, `follow-redirects`, `ip-address` | 🟠 high | dev-time tooling → lockfile refresh via `npm update` (vite 7.3.2, rollup 4.59+, …) |

**Lesson for the report:** the two *critical* alerts both traced back to a single mistake — depending
on the obsolete `firestore` npm package (probably picked by name instead of the real `firebase/firestore`
module). It was never imported anywhere, yet it silently dragged a 2016 dependency tree into the build.
Unused dependencies are attack surface; removal is the best patch.

**Verification:** `npm audit` → **0 vulnerabilities**; `ng build` green. Manual smoke of login +
registration on the updated tree still pending. GitHub will auto-close the alerts when the
refreshed `package-lock.json` is pushed.

Manifest: `Webshop/package-lock.json`.

| # | Severity | Package | Vulnerable range | Patched | GHSA | Status / fix applied |
|---|----------|---------|------------------|---------|------|----------------------|
| 34 | high | @angular/common | >=20.0.0-next.0, <20.3.14 | 20.3.14 | GHSA-58c5-g7wp-6w37 | ✅ 20.3.24 via npm update |
| 37 | high | @angular/compiler | <20.3.15 | 20.3.15 | GHSA-v4hv-rgfq-gp49 | ✅ 20.3.24 via npm update |
| 42 | high | @angular/compiler | <20.3.16 | 20.3.16 | GHSA-jrmj-c5cx-3cw6 | ✅ 20.3.24 via npm update |
| 75 | high | @angular/compiler | <20.3.18 | 20.3.18 | GHSA-g93w-mfhg-p222 | ✅ 20.3.24 via npm update |
| 43 | high | @angular/core | <20.3.16 | 20.3.16 | GHSA-jrmj-c5cx-3cw6 | ✅ 20.3.24 via npm update |
| 67 | high | @angular/core | <=20.3.16 | 20.3.17 | GHSA-prjf-86w9-mfqv | ✅ 20.3.24 via npm update |
| 76 | high | @angular/core | <20.3.18 | 20.3.18 | GHSA-g93w-mfhg-p222 | ✅ 20.3.24 via npm update |
| 110 | medium | @protobufjs/utf8 | <=1.1.0 | 1.1.1 | GHSA-q6x5-8v7m-xcrf | ✅ firebase 12.14.0 tree via npm update |
| 108 | high | fast-uri | <=3.1.0 | 3.1.1 | GHSA-q3j6-qgpj-74h6 | ✅ firebase 12.14.0 tree via npm update |
| 109 | high | fast-uri | <=3.1.1 | 3.1.2 | GHSA-v39h-62p7-jpjc | ✅ firebase 12.14.0 tree via npm update |
| 20 | medium | firebase | <10.9.0 | 10.9.0 | GHSA-3wf4-68gx-mph8 | ✅ removed unused firestore pkg (nested firebase 2.4.2 gone) |
| 81 | high | flatted | <=3.4.1 | 3.4.2 | GHSA-rf6f-7fwh-wjgh | ✅ lockfile refresh via npm update |
| 103 | medium | follow-redirects | <=1.15.11 | 1.16.0 | GHSA-r4q5-vmmm-2653 | ✅ firebase 12.14.0 tree via npm update |
| 33 | high | glob | >=10.2.0, <10.5.0 | 10.5.0 | GHSA-5j98-mcp5-4vw2 | ✅ lockfile refresh via npm update |
| 72 | high | immutable | >=5.0.0, <5.1.5 | 5.1.5 | GHSA-wf6x-7x77-mvgw | ✅ lockfile refresh via npm update |
| 105 | medium | ip-address | <=10.1.0 | 10.1.1 | GHSA-v2v4-37r5-5v8g | ✅ firebase 12.14.0 tree via npm update |
| 13 | medium | lodash | <4.17.5 | 4.17.5 | GHSA-fvqr-27wr-82fm | ✅ removed unused firestore pkg (lodash 3.10.1 gone) |
| 14 | high | lodash | <4.17.11 | 4.17.11 | GHSA-4xc9-xhrj-v574 | ✅ removed unused firestore pkg (lodash 3.10.1 gone) |
| 15 | critical | lodash | <4.17.12 | 4.17.12 | GHSA-jf85-cpcp-j695 | ✅ removed unused firestore pkg (lodash 3.10.1 gone) |
| 18 | high | lodash | <4.17.21 | 4.17.21 | GHSA-35jh-r3h4-6jhm | ✅ removed unused firestore pkg (lodash 3.10.1 gone) |
| 19 | high | lodash | >=3.7.0, <4.17.19 | 4.17.19 | GHSA-p6mc-m468-83gw | ✅ removed unused firestore pkg (lodash 3.10.1 gone) |
| 47 | medium | lodash | >=4.0.0, <=4.17.22 | 4.17.23 | GHSA-xxjr-mmjv-4gpg | ✅ 4.18.1 via npm update |
| 99 | high | lodash | >=4.0.0, <=4.17.23 | 4.18.0 | GHSA-r5fr-rjxr-66jc | ✅ 4.18.1 via npm update |
| 101 | medium | lodash | <=4.17.23 | 4.18.0 | GHSA-f23m-r3pf-42rh | ✅ 4.18.1 via npm update |
| 65 | high | minimatch | >=9.0.0, <9.0.7 | 9.0.7 | GHSA-7r86-cg39-jmmj | ✅ lockfile refresh via npm update |
| 71 | high | minimatch | <3.1.3 | 3.1.3 | GHSA-7r86-cg39-jmmj | ✅ lockfile refresh via npm update |
| 84 | medium | picomatch | >=4.0.0, <4.0.4 | 4.0.4 | GHSA-3v7f-55p6-f55p | ✅ lockfile refresh via npm update |
| 95 | medium | picomatch | <2.3.2 | 2.3.2 | GHSA-3v7f-55p6-f55p | ✅ lockfile refresh via npm update |
| 125 | medium | postcss | <8.5.10 | 8.5.10 | GHSA-qx2v-qp2m-jg93 | ✅ lockfile refresh via npm update |
| 104 | critical | protobufjs | <7.5.5 | 7.5.5 | GHSA-xq3m-2v4x-88gg | ✅ firebase 12.14.0 tree via npm update |
| 111 | medium | protobufjs | <=7.5.5 | 7.5.6 | GHSA-q6x5-8v7m-xcrf | ✅ firebase 12.14.0 tree via npm update |
| 112 | high | protobufjs | <=7.5.5 | 7.5.6 | GHSA-75px-5xx7-5xc7 | ✅ firebase 12.14.0 tree via npm update |
| 113 | high | protobufjs | <=7.5.5 | 7.5.6 | GHSA-jvwf-75h9-cwgg | ✅ firebase 12.14.0 tree via npm update |
| 114 | medium | protobufjs | <=7.5.5 | 7.5.6 | GHSA-fx83-v9x8-x52w | ✅ firebase 12.14.0 tree via npm update |
| 115 | medium | protobufjs | <=7.5.5 | 7.5.6 | GHSA-2pr8-phx7-x9h3 | ✅ firebase 12.14.0 tree via npm update |
| 116 | high | protobufjs | <=7.5.5 | 7.5.6 | GHSA-66ff-xgx4-vchm | ✅ firebase 12.14.0 tree via npm update |
| 117 | high | protobufjs | <=7.5.5 | 7.5.6 | GHSA-685m-2w69-288q | ✅ firebase 12.14.0 tree via npm update |
| 121 | medium | protobufjs | <=7.5.7 | 7.5.8 | GHSA-jggg-4jg4-v7c6 | ✅ firebase 12.14.0 tree via npm update |
| 41 | medium | qs | <6.14.1 | 6.14.1 | GHSA-6rw7-vpxm-498p | ✅ lockfile refresh via npm update |
| 50 | low | qs | >=6.7.0, <=6.14.1 | 6.14.2 | GHSA-w7fw-mjwx-w883 | ✅ lockfile refresh via npm update |
| 119 | medium | qs | >=6.11.1, <=6.15.1 | 6.15.2 | GHSA-q8mj-m7cp-5q26 | ✅ lockfile refresh via npm update |
| 60 | high | rollup | >=4.0.0, <4.59.0 | 4.59.0 | GHSA-mw96-cpmx-2vgc | ✅ lockfile refresh via npm update |
| 44 | high | tar | <=7.5.2 | 7.5.3 | GHSA-8qq5-rm4j-mr97 | ✅ lockfile refresh via npm update |
| 45 | high | tar | <=7.5.3 | 7.5.4 | GHSA-r6q2-hw4h-h46w | ✅ lockfile refresh via npm update |
| 48 | high | tar | <7.5.7 | 7.5.7 | GHSA-34x7-hfp2-rc4v | ✅ lockfile refresh via npm update |
| 51 | high | tar | <7.5.8 | 7.5.8 | GHSA-83g3-92jg-28cx | ✅ lockfile refresh via npm update |
| 73 | high | tar | <=7.5.9 | 7.5.10 | GHSA-qffp-2rhf-9h96 | ✅ lockfile refresh via npm update |
| 74 | high | tar | <=7.5.10 | 7.5.11 | GHSA-9ppj-qmqm-q256 | ✅ lockfile refresh via npm update |
| 21 | low | tmp | <=0.2.3 | 0.2.4 | GHSA-52f5-9888-hmc6 | ✅ lockfile refresh via npm update |
| 120 | high | tmp | <0.2.6 | 0.2.6 | GHSA-ph9p-34f9-6g65 | ✅ lockfile refresh via npm update |
| 22 | low | vite | >=6.0.0, <=6.3.5 | 6.3.6 | GHSA-g4jq-h2w9-997c | ✅ lockfile refresh via npm update |
| 23 | low | vite | >=6.0.0, <=6.3.5 | 6.3.6 | GHSA-jqfw-vq24-v9c3 | ✅ lockfile refresh via npm update |
| 27 | medium | vite | >=6.0.0, <=6.4.0 | 6.4.1 | GHSA-93m4-6634-74q7 | ✅ lockfile refresh via npm update |
| 91 | high | vite | >=6.0.0, <=6.4.1 | 6.4.2 | GHSA-p9ff-h696-f583 | ✅ lockfile refresh via npm update |
| 102 | medium | vite | <=6.4.1 | 6.4.2 | GHSA-4w7w-66w2-5vf9 | ✅ lockfile refresh via npm update |

---

## Out of scope (recorded, NOT remediated)

`EmailService` (Node/TS) — **25 open alerts** across `package.json` and `pnpm-lock.yaml`
(nodemailer, lodash, minimatch, glob, picomatch, brace-expansion, html-minifier, js-cookie,
js-yaml, mjml). `OrderService` (.NET) — no current alerts. Both services are **permanently
excluded from the SWD project scope** and are intentionally left unremediated. If the project scope
is ever changed to include them, an `EmailService` cluster can be cleared with `pnpm update` + lockfile refresh.

