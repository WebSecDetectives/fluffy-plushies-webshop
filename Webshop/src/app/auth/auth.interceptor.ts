import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { isOwnBackendUrl } from '../api/backend-urls';

/**
 * Attaches the JWT as a Bearer header, but only on requests to our own backends —
 * never leak the token to third parties.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!isOwnBackendUrl(req.url)) {
    return next(req);
  }

  const token = inject(AuthService).getToken();
  if (!token) {
    return next(req);
  }

  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};