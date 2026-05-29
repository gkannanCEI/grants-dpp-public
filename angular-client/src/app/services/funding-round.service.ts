import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, shareReplay } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { FundingRoundModel } from '../models/funding-round.model';

const BASE_URL = `${environment.apiUrl}/funding-rounds`;

/**
 * FundingRoundService — enterprise-grade with in-memory caching.
 *
 * Performance optimisations:
 *
 *  1. shareReplay(1) — deduplicates concurrent subscribers.
 *     If two components subscribe to getAll() at the same time,
 *     only ONE HTTP request is made; both get the same response.
 *
 *  2. In-memory cache — after the first successful getAll() call,
 *     subsequent calls within the same browser session return instantly
 *     from memory without hitting the network.
 *     Cache is invalidated automatically on create / update / delete.
 *
 *  3. Request timeout via RxJS timeout() is NOT added here because
 *     Angular's HttpClient has no built-in timeout — add it at the
 *     interceptor level if needed (5 000ms is a reasonable default).
 */
@Injectable({ providedIn: 'root' })
export class FundingRoundService {

  /** In-memory cache of the full list — invalidated on write operations. */
  private cache$: Observable<FundingRoundModel[]> | null = null;

  constructor(private http: HttpClient) {}

  // ── Read ──────────────────────────────────────────────────────────────────

  /**
   * Returns all funding rounds.
   *
   * First call → HTTP GET → caches result with shareReplay(1).
   * Subsequent calls → returns cached Observable instantly (no network round-trip).
   * After any write (create/update/delete) → cache is cleared → next call re-fetches.
   */
  getAll(status?: string): Observable<FundingRoundModel[]> {
    // Status-filtered requests bypass the shared cache (different data sets)
    if (status) {
      const params = new HttpParams().set('status', status);
      return this.http.get<FundingRoundModel[]>(BASE_URL, { params });
    }

    // Use cache for the unfiltered list
    if (!this.cache$) {
      this.cache$ = this.http.get<FundingRoundModel[]>(BASE_URL).pipe(
        // shareReplay(1): multicasts to all subscribers + replays last value
        // to late subscribers — eliminates duplicate concurrent requests
        shareReplay({ bufferSize: 1, refCount: true }),
        catchError(err => {
          // On error, clear cache so next call retries the network
          this.cache$ = null;
          throw err;
        })
      );
    }

    return this.cache$;
  }

  /** Fetch a single funding round by id (detail screen — no caching needed). */
  getById(id: number): Observable<FundingRoundModel> {
    return this.http.get<FundingRoundModel>(`${BASE_URL}/${id}`);
  }

  // ── Write (all invalidate the list cache) ────────────────────────────────

  /** Create a new funding round and invalidate the list cache. */
  create(round: FundingRoundModel): Observable<FundingRoundModel> {
    return this.http.post<FundingRoundModel>(BASE_URL, round).pipe(
      tap(() => this.invalidateCache())
    );
  }

  /** Update an existing funding round and invalidate the list cache. */
  update(id: number, round: FundingRoundModel): Observable<FundingRoundModel> {
    return this.http.put<FundingRoundModel>(`${BASE_URL}/${id}`, round).pipe(
      tap(() => this.invalidateCache())
    );
  }

  /** Delete a funding round and invalidate the list cache. */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${BASE_URL}/${id}`).pipe(
      tap(() => this.invalidateCache())
    );
  }

  /** Submit a round for approval and invalidate the list cache. */
  submitForApproval(id: number): Observable<FundingRoundModel> {
    return this.http.post<FundingRoundModel>(`${BASE_URL}/${id}/submit`, {}).pipe(
      tap(() => this.invalidateCache())
    );
  }

  /** Save as draft — create or update depending on presence of id. */
  saveDraft(round: FundingRoundModel): Observable<FundingRoundModel> {
    const payload = { ...round, status: 'DRAFT' };
    return round.id ? this.update(round.id, payload) : this.create(payload);
  }

  // ── Cache management ──────────────────────────────────────────────────────

  /**
   * Clears the in-memory cache so the next getAll() call hits the network.
   * Called automatically after every create / update / delete.
   * Can also be called manually to force a refresh.
   */
  invalidateCache(): void {
    this.cache$ = null;
  }
}
