import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NavigationService {
  private baseUrl = `${environment.apiUrl}/navigation`;

  constructor(private http: HttpClient) { }

  getPageTitle(pageId: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/title/${pageId}`);
  }
}
