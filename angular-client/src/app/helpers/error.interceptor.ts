import {
  Injectable
} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Global HTTP error interceptor.
 *
 * Responsibilities:
 *  - 401 Unauthorized → auto-logout + redirect to /login
 *  - 403 Forbidden     → redirect to /error with context
 *  - All other errors  → re-throw so the originating component
 *                        can display inline error messages.
 *
 * Intentionally does NOT redirect on 404/409/500 etc. so that
 * feature components can handle those gracefully in their own
 * error callbacks (e.g. show an inline banner instead of a
 * full-page error redirect).
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {

    return next.handle(request).pipe(
      catchError((err: unknown) => {

        if (err instanceof HttpErrorResponse) {

          switch (err.status) {

            case 401:
              // Session expired or invalid credentials — force re-login
              console.warn('[ErrorInterceptor] 401 Unauthorized — logging out');
              this.authService.logout();
              // logout() already navigates to /login
              break;

            case 403:
              // Authenticated but not authorised for this resource
              console.warn('[ErrorInterceptor] 403 Forbidden');
              this.router.navigate(['/error'], {
                queryParams: {
                  code: 403,
                  message: 'You do not have permission to access this resource.'
                }
              });
              break;

            default:
              // 404, 409, 422, 500, network errors, etc.
              // Log for observability but let the component handle display.
              console.error(
                `[ErrorInterceptor] HTTP ${err.status} on ${request.method} ${request.url}`,
                err.error ?? err.message
              );
              break;
          }
        } else {
          // Non-HTTP error (e.g. network failure, timeout)
          console.error('[ErrorInterceptor] Non-HTTP error:', err);
        }

        // Always re-throw so the calling Observable's error() callback fires
        const message =
          err instanceof HttpErrorResponse
            ? (err.error?.message ?? err.message ?? err.statusText)
            : 'An unexpected error occurred';

        return throwError(() => new Error(message));
      })
    );
  }
}
