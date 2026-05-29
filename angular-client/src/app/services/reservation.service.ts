import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Reservation } from '../models/reservation.model';

/**
 * Service for managing reservations via the REST API.
 *
 * Note: environment.apiUrl already contains '/api' (e.g. 'http://localhost:8080/api')
 * so we only append the resource path here.
 */
@Injectable({ providedIn: 'root' })
export class ReservationService {

  private readonly baseUrl = `${environment.apiUrl}/reservations`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.baseUrl);
  }

  get(id: number): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.baseUrl}/${id}`);
  }

  create(data: Partial<Reservation>): Observable<Reservation> {
    return this.http.post<Reservation>(this.baseUrl, data);
  }

  update(id: number, data: Partial<Reservation>): Observable<Reservation> {
    return this.http.put<Reservation>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  deleteAll(): Observable<void> {
    return this.http.delete<void>(this.baseUrl);
  }

  findByTitle(title: string): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.baseUrl}?title=${encodeURIComponent(title)}`);
  }
}
