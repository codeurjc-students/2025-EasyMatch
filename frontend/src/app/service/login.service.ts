import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError, map, Observable, BehaviorSubject, tap, pipe, switchMap, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse } from '../models/auth/auth-response.model';
import { LoginRequest } from '../models/auth/login-request.model';
import { UserService } from './user.service';
import { User } from '../models/user.model';


@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private apiUrl = environment.apiUrl;

  private https = inject(HttpClient);
  private userService = inject(UserService);

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private sessionReadySubject = new BehaviorSubject(false);
  sessionReady$ = this.sessionReadySubject.asObservable();
  isAdmin$ = this.currentUser$.pipe(
    map(user => !!user && user.roles.includes('ADMIN'))
  );

  constructor() {
    this.restoreSession();
  }
  

  public login(loginRequest: LoginRequest): Observable<User | null> {
    return this.https.post<AuthResponse>(
      `${this.apiUrl}/auth/login`,
      loginRequest,
      { withCredentials: true }
    ).pipe(

      switchMap(() => this.userService.getCurrentUser().pipe(
        catchError(() => of(null))
      )),
      tap(user => {
        this.currentUserSubject.next(user);
      }),
      catchError(this.handleError) 
    );
  }

  restoreSession(): void {
    this.userService.getCurrentUser({
      headers: { 'X-Skip-Interceptor': 'true' }
    }).pipe(
      catchError(() => of(null)),
      tap(user => this.currentUserSubject.next(user)),
      tap(() => this.sessionReadySubject.next(true))
    ).subscribe();
  }

  logout(): Observable<AuthResponse> {
    return this.https.post<AuthResponse>(`${this.apiUrl}/auth/logout`,{},{ withCredentials: true }).pipe(
      tap(() => this.currentUserSubject.next(null))
    );
  }

  get currentUser$(): Observable<User | null> {
    return this.currentUserSubject.asObservable();
  }
  
  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isLogged(): boolean {
    return this.currentUserSubject.value !== null;
  }

  private handleError(error: HttpErrorResponse) {

    if (error.status === 0) {
      console.error('Error conexión', error.error);
    } else {
      console.error('Backend error', error);
    }

    return throwError(() => new Error('Algo falló.'));

  }

  
}
