import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private API_URL = '/api/v1/auth';
  isAuthenticated = signal(false);

  constructor(private http: HttpClient) {}

  checkAuthStatus(): Observable<boolean> {
    return this.http.get(`${this.API_URL}/status`, { withCredentials: true }).pipe(
      map(() => {
        this.isAuthenticated.set(true);
        return true;
      }),
      catchError(() => {
        this.isAuthenticated.set(false);
        return of(false);
      })
    );
  }
}
