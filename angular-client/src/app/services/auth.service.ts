import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Router } from '@angular/router';

const AUTH_API = `${environment.apiUrl}/auth/`;

/** Shape of the user object stored in localStorage and emitted by currentUser$ */
export interface StoredUser {
  username: string;
  role: string;
  /** Plaintext password kept only for HTTP Basic Auth header generation.
   *  Replace with a short-lived JWT once the backend issues tokens. */
  password: string;
  token?: string;
}

/**
 * Authentication service.
 *
 * Manages the current user session via a BehaviorSubject so any component
 * can reactively subscribe to login/logout state changes.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly STORAGE_KEY = 'currentUser';

  private currentUserSubject = new BehaviorSubject<StoredUser | null>(
    this.loadFromStorage()
  );

  /** Observable stream of the current user — emits null when logged out. */
  public currentUser$: Observable<StoredUser | null> =
    this.currentUserSubject.asObservable();

  /** @deprecated Use currentUser$ instead */
  public currentUser = this.currentUser$;

  constructor(private http: HttpClient, private router: Router) {}

  // ── Login ────────────────────────────────────────────────────────────────

  login(username: string, password: string): Observable<any> {
    return this.http
      .post<{ message: string; username: string; role: string }>(
        AUTH_API + 'login',
        { username, password }
      )
      .pipe(
        tap((response) => {
          // Store password alongside server response so the AuthInterceptor
          // can construct the Basic Auth header on subsequent requests.
          const user: StoredUser = {
            username: response.username,
            role:     response.role,
            password              // kept for Basic Auth header construction
          };
          this.persistUser(user);
        })
      );
  }

  // ── Register ─────────────────────────────────────────────────────────────

  register(username: string, password: string, role: string): Observable<any> {
    return this.http.post(AUTH_API + 'register', { username, password, role });
  }

  // ── Logout ───────────────────────────────────────────────────────────────

  logout(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  // ── State helpers ────────────────────────────────────────────────────────

  isLoggedIn(): boolean {
    const user = this.currentUserSubject.value;
    return user !== null && !!user.username;
  }

  getUserRole(): string {
    return this.currentUserSubject.value?.role ?? '';
  }

  getUsername(): string {
    return this.currentUserSubject.value?.username ?? '';
  }

  getCurrentUser(): StoredUser | null {
    return this.currentUserSubject.value;
  }

  // ── Private helpers ──────────────────────────────────────────────────────

  private loadFromStorage(): StoredUser | null {
    try {
      const raw = localStorage.getItem(this.STORAGE_KEY);
      return raw ? (JSON.parse(raw) as StoredUser) : null;
    } catch {
      return null;
    }
  }

  private persistUser(user: StoredUser): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(user));
    this.currentUserSubject.next(user);
  }
}
