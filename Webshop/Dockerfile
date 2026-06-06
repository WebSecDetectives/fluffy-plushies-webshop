FROM docker.io/library/node:22-alpine@sha256:968df39aedcea65eeb078fb336ed7191baf48f972b4479711397108be0966920 AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
# environment.ts already uses same-origin paths (/identity, /inventory); nginx proxies them.
RUN npm run build

FROM docker.io/nginxinc/nginx-unprivileged:1.27-alpine@sha256:65e3e85dbaed8ba248841d9d58a899b6197106c23cb0ff1a132b7bfe0547e4c0
COPY --from=build /app/dist/fluffy_plushies_web_app/browser /usr/share/nginx/html
COPY webshop-nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 8080
