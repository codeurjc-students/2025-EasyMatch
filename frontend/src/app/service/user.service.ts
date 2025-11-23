import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { User } from '../models/user.model';
import { Match } from '../models/match.model';


@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/me`, { withCredentials: true });
  }

  deleteUser(id: number): Observable<User> {
    return this.http.delete<User>(`${this.apiUrl}/users/${id}`,{withCredentials : true});
  }

  registerUser(userData: Partial<User>): Observable<User> {
    const payload = {
      ...userData,
      birthDate: userData.birthDate instanceof Date ? userData.birthDate.toISOString() : userData.birthDate
    };
    return this.http.post<User>(`${this.apiUrl}/users/`, payload).pipe(
      map(response => ({
        ...response,
        birthDate: new Date(response.birthDate) 
      }))
    );;
  }

  getUserMatches(userId: number): Observable<Match[]> {
    return this.http.get<Match[]>(`${this.apiUrl}/users/${userId}/matches/`, {
      withCredentials: true
    });
  }

}
