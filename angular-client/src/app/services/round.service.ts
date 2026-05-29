import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Round } from '../models/round.model';

/**
 * Service for managing funding rounds via the REST API.
 *
 * Base URL is derived from the environment configuration to ensure
 * the correct API endpoint is used per deployment environment.
 *
 * Note: environment.apiUrl already contains '/api' (e.g. 'http://localhost:8080/api')
 * so we only append the resource path here — never double-prefix with '/api'.
 */
@Injectable({ providedIn: 'root' })
export class RoundService {

  private readonly baseUrl = `${environment.apiUrl}/rounds`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Round[]> {
    return this.http.get<Round[]>(this.baseUrl);
  }

  get(id: number): Observable<Round> {
    return this.http.get<Round>(`${this.baseUrl}/${id}`);
  }

  create(data: Partial<Round>): Observable<Round> {
    return this.http.post<Round>(this.baseUrl, data);
  }

  update(id: number, data: Partial<Round>): Observable<Round> {
    return this.http.put<Round>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
