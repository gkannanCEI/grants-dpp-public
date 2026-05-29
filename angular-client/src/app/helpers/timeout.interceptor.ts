import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { timeout, catchError } from 'rxjs/operators';
import { throwError, TimeoutError } from 'rxjs';

/**
 * Request Timeout Interceptor — enterprise-grade defence against hung requests.
 *
 * Problem being solved:
 *  Without a timeout, if the Spring Boot server is slow to respond (e.g. cold
 *  DB connection pool, slow query, network hiccup), the Angular HttpClient
 *  will wait indefinitely — the user sees "Loading..." forever with no
 *  feedback and no way to recover.
 *
 * Solution:
 *  Every outgoing request gets a 10-second timeout.
 *  If the server hasn't responded within 10s, the observable errors with a
 *  clear "Request timed out" message that the component's error handler
 *  can display as an inline banner.
 *
 * Customisation per-request:
 *  Add header 'X-Request-Timeout: 30000' to override for specific long-running
 *  endpoints (e.g. report generation, bulk exports).
 */
@Injectable()
export class TimeoutInterceptor implements HttpInterceptor {

  /** Default timeout for all API requests in milliseconds. */
  private readonly DEFAULT_TIMEOUT_MS = 10_000;

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {

    // Allow per-request timeout override via custom header
    const customTimeout = req.headers.get('X-Request-Timeout');
    const timeoutMs = customTimeout
      ? parseInt(customTimeout, 10)
      : this.DEFAULT_TIMEOUT_MS;

    // Strip the custom header before sending to the server
    const cleanReq = customTimeout
      ? req.clone({ headers: req.headers.delete('X-Request-Timeout') })
      : req;

    return next.handle(cleanReq).pipe(
      timeout(timeoutMs),
      catchError(err => {
        if (err instanceof TimeoutError) {
          console.error(
            `[TimeoutInterceptor] Request timed out after ${timeoutMs}ms: ` +
            `${req.method} ${req.url}`
          );
          return throwError(() => new Error(
            `Request timed out after ${timeoutMs / 1000}s. ` +
            `Please check your connection and try again.`
          ));
        }
        // Re-throw all other errors unchanged
        return throwError(() => err);
      })
    );
  }
}
