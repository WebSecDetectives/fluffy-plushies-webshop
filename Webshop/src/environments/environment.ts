export const environment = {
  // Same-origin relative paths in every environment: ng serve proxies them to the
  // local backends (proxy.conf.json), the Docker image's nginx proxies them to the
  // backend containers (webshop-nginx.conf). No CORS either way.
  baseUrlIdentity: '/identity',
  baseUrlInventory: '/inventory',
};
