import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

/**
 * Application route definitions.
 *
 * All feature routes are lazy-loaded via loadComponent() to minimise the
 * initial bundle size — the code for each screen is only downloaded when
 * the user first navigates to that route.
 *
 * Public routes (login) are also lazy-loaded so the login screen code is
 * not included in the initial bootstrap bundle.
 *
 * canActivate: [AuthGuard] on every protected route ensures unauthenticated
 * users are redirected to /login before the component is even loaded.
 */
export const routes: Routes = [
  // ── Public routes ──────────────────────────────────────────────────────
  {
    path: 'login',
    loadComponent: () =>
      import('./components/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'error',
    loadComponent: () =>
      import('./components/error-page/error-page.component').then(m => m.ErrorPageComponent)
  },

  // ── Protected routes ───────────────────────────────────────────────────
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'funding-intelligence',
    loadComponent: () =>
      import('./components/funding-intelligence/funding-intelligence.component')
        .then(m => m.FundingIntelligenceComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'create-funding-round',
    loadComponent: () =>
      import('./components/create-funding-round/create-funding-round.component')
        .then(m => m.CreateFundingRoundComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'create-funding-round/:id',
    loadComponent: () =>
      import('./components/create-funding-round/create-funding-round.component')
        .then(m => m.CreateFundingRoundComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'reservations',
    loadComponent: () =>
      import('./components/generic-page/generic-page.component').then(m => m.GenericPageComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'members',
    loadComponent: () =>
      import('./components/generic-page/generic-page.component').then(m => m.GenericPageComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'disbursements',
    loadComponent: () =>
      import('./components/generic-page/generic-page.component').then(m => m.GenericPageComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'compliance',
    loadComponent: () =>
      import('./components/generic-page/generic-page.component').then(m => m.GenericPageComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'reports-analytics',
    loadComponent: () =>
      import('./components/generic-page/generic-page.component').then(m => m.GenericPageComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'communications',
    loadComponent: () =>
      import('./components/generic-page/generic-page.component').then(m => m.GenericPageComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'help-support',
    loadComponent: () =>
      import('./components/generic-page/generic-page.component').then(m => m.GenericPageComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'settings',
    loadComponent: () =>
      import('./components/generic-page/generic-page.component').then(m => m.GenericPageComponent),
    canActivate: [AuthGuard]
  },

  // ── Fallback routes ────────────────────────────────────────────────────
  { path: '',   redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'error' }
];
