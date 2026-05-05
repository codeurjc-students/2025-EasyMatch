import { Injectable} from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { catchError, map, Observable, of, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { User } from '../models/user.model';
import { Match } from '../models/match.model';
import { LevelHistory } from '../models/level-history.model';
import { UserSportProfile } from '../models/user-sport-profile.model';
import { Sport } from '../models/sport.model';


@Injectable({ providedIn: 'root' })
export class UserService {
  
  
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getCurrentUser(options?: { headers?: Record<string, string> }): Observable<User | null> {
    return this.http.get<User>(`${this.apiUrl}/users/me`, {
        ...options 
      }).pipe(
        catchError(err => {
          if (err.status === 500) return of(null);
          return throwError(() => err);
        })
    );
  }

  deleteUser(id: number): Observable<User> {
    return this.http.delete<User>(`${this.apiUrl}/users/${id}`);
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
    return this.http.get<Match[]>(`${this.apiUrl}/users/${userId}/matches/`);
  }

  getAllUsers(page = 0, 
    size = 10, 
    sort = 'id,asc'
  ): Observable<{
    content: User[];
    totalElements: number;
    totalPages: number;
    number: number;
  }> 
  {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);

    return this.http.get<{
      content: User[];
      totalElements: number;
      totalPages: number;
      number: number;
    }>(`${this.apiUrl}/users/`, { params }).pipe(
      map(response => ({ ...response, content: response.content }))
    );
  }

  getUserImage(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/users/${id}/image`, { responseType: 'blob'});
  }

  updateUser(editingId: number, payload: Partial<User>) : Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/${editingId}`, payload);
  }

  getUserById(id: number) : Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/${id}`);
  }

  replaceUserImage(id: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('imageFile', file);
    return this.http.put(`${this.apiUrl}/users/${id}/image`, formData);
  }

  getUserSportHistory(userId: number, sportId: number) {
    return this.http.get<LevelHistory[]>(
      `${this.apiUrl}/users/${userId}/sports/${sportId}/history`);
  }

  getUserSportProfile(userId: number, sportId: number) {
    return this.http.get<UserSportProfile>(
      `${this.apiUrl}/users/${userId}/sports/${sportId}/profile`
    );
  }

  updateSportProfile(userId: number, sportId: number, profile: Partial<UserSportProfile>) {
    return this.http.put(
      `${this.apiUrl}/users/${userId}/sports/${sportId}/profile`,
      profile
    );
  }

  addSportToUser(userId: number, sportId: number, profile: Partial<UserSportProfile>) {
    return this.http.post(
      `${this.apiUrl}/users/${userId}/sports/${sportId}/profile`,
      profile
    );
  }

  getUserSports(userId: number) {
    return this.http.get<Sport[]>(
      `${this.apiUrl}/users/${userId}/sports`
    );
  }

}
