import { provideHttpClient, withInterceptorsFromDi, HTTP_INTERCEPTORS } from '@angular/common/http';
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { AuthInterceptor } from './app/services/auth.interceptor';
import { ErrorInterceptor } from './app/helpers/error.interceptor';
import { TimeoutInterceptor } from './app/helpers/timeout.interceptor';
import { ErrorHandler } from '@angular/core';
import { GlobalErrorHandler } from './app/services/global-error-handler.service';

/**
 * Interceptor execution order (first registered = outermost wrapper):
 *
 *  Request flow  (outward):  Auth → Timeout → Error → HttpBackend
 *  Response flow (inward):   HttpBackend → Error → Timeout → Auth
 *
 *  1. AuthInterceptor   — attaches Authorization header before anything else
 *  2. TimeoutInterceptor — enforces 10s deadline on every request
 *  3. ErrorInterceptor  — normalises 401/403/5xx into typed errors
 */
bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),

    // Order matters — interceptors are applied in registration order
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor,    multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: TimeoutInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor,   multi: true },

    // Global uncaught error handler
    { provide: ErrorHandler, useClass: GlobalErrorHandler }
  ]
}).catch(err => console.error(err));
