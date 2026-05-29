import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Authentication interceptor.
 *
 * Attaches the appropriate Authorization header to every outgoing request
 * based on what was stored in localStorage after login.
 *
 * Strategy (in priority order):
 *  1. Bearer token  — used when the backend issues a JWT (future-proof)
 *  2. Basic auth    — used for the current HTTP Basic Auth backend.
 *                     Credentials are Base64-encoded per RFC 7617.
 *  3. Pass-through  — login/register calls carry no credentials.
 *
 * Security note: withCredentials is NOT set here because the backend uses
 * stateless Basic Auth (not cookies). Setting it would trigger CORS
 * preflight issues for non-credentialed origins.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {

    const stored = localStorage.getItem('currentUser');
    const user: {
      token?: string;
      username?: string;
      password?: string;
    } | null = stored ? JSON.parse(stored) : null;

    // ── Strategy 1: Bearer token (JWT) ─────────────────────────────────────
    if (user?.token) {
      return next.handle(
        req.clone({
          setHeaders: { Authorization: `Bearer ${user.token}` }
        })
      );
    }

    // ── Strategy 2: HTTP Basic Auth ────────────────────────────────────────
    // The backend validates username:password against BCrypt hashes in the DB.
    // The password is stored in localStorage only to enable this flow;
    // in production this would be replaced with a short-lived JWT.
    if (user?.username && user?.password) {
      const credentials = btoa(`${user.username}:${user.password}`);
      return next.handle(
        req.clone({
          setHeaders: { Authorization: `Basic ${credentials}` }
        })
      );
    }

    // ── Strategy 3: Pass-through (public endpoints) ─────────────────────────
    return next.handle(req);
  }
}
