import { environment } from '../../enviroments/enviroment';

/**
 * True if the URL targets one of our own backend services (Identity/Inventory).
 */
export function isOwnBackendUrl(url: string): boolean {
  return url.startsWith(environment.baseUrlIdentity) || url.startsWith(environment.baseUrlInventory);
}