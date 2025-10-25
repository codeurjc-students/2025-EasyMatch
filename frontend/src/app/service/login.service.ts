import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError, map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse } from '../models/auth/auth-response.model';
import { LoginRequest } from '../models/auth/login-request.model';


@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private apiUrl = environment.apiUrl;

  constructor(private https: HttpClient) {}
  

  public login(loginRequest: LoginRequest): Observable<AuthResponse> {
		return this.https.post<AuthResponse>(`${this.apiUrl}`+'/auth/login', loginRequest, { withCredentials: true});
	}

  public logout(): Observable<AuthResponse> {
		return this.https.post<AuthResponse>(this.apiUrl, {}, { withCredentials: true });
	}
}
