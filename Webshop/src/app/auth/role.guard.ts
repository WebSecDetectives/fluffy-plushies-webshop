import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { CurrentUser } from './auth.service';

/**
 * Route guard allowing only the given roles; everyone else is redirected home.
 * UX convenience only — the backend enforces authorization on every request.
 */
export function roleGuard(...allowedRoles: Array<CurrentUser['role']>): CanActivateFn {
  return () => {
    const user = inject(AuthService).currentUser();
    if (user && allowedRoles.includes(user.role)) {
      return true;
    }
    return inject(Router).createUrlTree(['/']);
  };
}
