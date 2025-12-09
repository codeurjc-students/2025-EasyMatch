import { Injectable, Resource } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
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
    }>(`${this.apiUrl}/users/`, { params, withCredentials: true }).pipe(
      map(response => ({ ...response, content: response.content }))
    );
  }

  getUserImage(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/users/${id}/image`, { responseType: 'blob', withCredentials: true });
  }

  updateUser(editingId: number, payload: Partial<User>) : Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/${editingId}`, payload, { withCredentials: true });
  }

  getUserById(id: number) : Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/${id}`, { withCredentials: true });
  }

  replaceUserImage(id: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('imageFile', file);
    return this.http.put(`${this.apiUrl}/users/${id}/image`, formData, { withCredentials: true });
  }
}
