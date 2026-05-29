import { ErrorHandler, Injectable, Injector, NgZone } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Global uncaught error handler.
 *
 * Catches only truly unrecoverable runtime errors that escape all other
 * error boundaries (interceptors, component try/catch, etc.).
 *
 * Critically: Angular emits several internal non-fatal errors (e.g.
 * ExpressionChangedAfterCheckedError in dev mode, NavigationError from
 * the router) that should NOT redirect the user. This handler filters
 * those out so the user is only redirected for genuine application crashes.
 *
 * HTTP errors (401, 403, 500, timeouts) are handled by ErrorInterceptor
 * and TimeoutInterceptor — they never reach this handler.
 */
@Injectable()
export class GlobalErrorHandler implements ErrorHandler {

  /**
   * Patterns that identify known non-fatal Angular-internal errors.
   * Matching errors are logged but do NOT trigger a redirect.
   */
  private readonly NON_FATAL_PATTERNS: RegExp[] = [
    /ExpressionChangedAfterItHasBeenCheckedError/i,
    /NavigationError/i,
    /Cannot match any routes/i,
    /NG0/,                  // Angular internal error codes (dev mode)
    /ResizeObserver loop/i  // Browser-level benign warning
  ];

  constructor(private injector: Injector, private zone: NgZone) {}

  handleError(error: unknown): void {
    const message = error instanceof Error ? error.message : String(error);

    // Always log — even non-fatal errors should appear in the console
    console.error('[GlobalErrorHandler] Uncaught error:', error);

    // Do NOT redirect for known non-fatal patterns
    if (this.NON_FATAL_PATTERNS.some(pattern => pattern.test(message))) {
      console.warn('[GlobalErrorHandler] Non-fatal error — suppressing redirect:', message);
      return;
    }

    // HTTP errors (401/403/5xx) are already handled by interceptors.
    // If they somehow reach here, don't double-redirect.
    if (message.includes('Http failure response') || message.includes('HttpErrorResponse')) {
      console.warn('[GlobalErrorHandler] HTTP error reached global handler — already handled by interceptor');
      return;
    }

    // Genuine unrecoverable crash — navigate to error page
    const router = this.injector.get(Router);
    this.zone.run(() => {
      router.navigate(['/error'], {
        queryParams: {
          code:    'APP',
          message: 'A critical application error occurred.'
        },
        skipLocationChange: true
      });
    });
  }
}
