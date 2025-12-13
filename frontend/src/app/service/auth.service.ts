import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = environment.apiUrl;
  isAuthenticated = signal(false);

  constructor(private http: HttpClient) {}

  checkAuthStatus(): Observable<boolean> {
    return this.http.get(`${this.apiUrl}/auth/status`, { withCredentials: true }).pipe(
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
