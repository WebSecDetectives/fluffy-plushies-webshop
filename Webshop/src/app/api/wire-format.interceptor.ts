import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs';
import { isOwnBackendUrl } from './backend-urls';
import { toCamelCaseKeys, toSnakeCaseKeys } from './case-converter';

/**
 * Converts JSON keys on requests to our own backends: outgoing request body
 * keys camelCase → snake_case, incoming response body keys snake_case → camelCase.
 * Requests to other hosts pass through unchanged.
 */
export const wireFormatInterceptor: HttpInterceptorFn = (req, next) => {
  if (!isOwnBackendUrl(req.url)) {
    return next(req);
  }

  const hasJsonBody =
    req.body !== null &&
    typeof req.body === 'object' &&
    !(req.body instanceof FormData) &&
    !(req.body instanceof Blob);

  const outgoing = hasJsonBody ? req.clone({ body: toSnakeCaseKeys(req.body) }) : req;

  return next(outgoing).pipe(
    map(event =>
      event instanceof HttpResponse && event.body !== null && typeof event.body === 'object'
        ? event.clone({ body: toCamelCaseKeys(event.body) })
        : event
    )
  );
};