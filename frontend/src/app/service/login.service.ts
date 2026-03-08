import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError, map, Observable, BehaviorSubject, tap, pipe } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse } from '../models/auth/auth-response.model';
import { LoginRequest } from '../models/auth/login-request.model';


@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private apiUrl = environment.apiUrl;

  currentUserLoginOn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  currentUserData: BehaviorSubject<String> =new BehaviorSubject<String>("");

  constructor(private https: HttpClient) {
    this.currentUserLoginOn=new BehaviorSubject<boolean>(sessionStorage.getItem("token") != null);
    this.currentUserData=new BehaviorSubject<String>(sessionStorage.getItem("token") || "");
  }
  

  public login(loginRequest: LoginRequest): Observable<any> {
		return this.https.post<any>(`${this.apiUrl}`+'/auth/login', loginRequest, { withCredentials: true}).pipe(
      tap((userData) => {
        sessionStorage.setItem("token", userData.token);
        sessionStorage.setItem("authorities", userData.authorities);
        this.currentUserData.next(userData.token);
        this.currentUserLoginOn.next(true);
      }),
      map((userData)=> userData),
      catchError(this.handleError)
    );;
	}

  public logout(): Observable<AuthResponse> {
		return this.https.post<AuthResponse>(`${this.apiUrl}`+'/auth/logout', {}, { withCredentials: true });
	}

  private handleError(error:HttpErrorResponse){
    if(error.status===0){
      console.error('Se ha producio un error ', error.error);
    }
    else{
      console.error('Backend retornó el código de estado ', error);
    }
    return throwError(()=> new Error('Algo falló. Por favor intente nuevamente.'));
  }

  get userData():Observable<String>{
    return this.currentUserData.asObservable();
  }

  get userLoginOn(): Observable<boolean>{
    return this.currentUserLoginOn.asObservable();
  }

  get userToken():String{
    return this.currentUserData.value;
  }

  isAdmin(): boolean {
    const roles = sessionStorage.getItem("authorities");

    if (!roles) return false;

    return roles.includes("ROLE_ADMIN");
  }
}
