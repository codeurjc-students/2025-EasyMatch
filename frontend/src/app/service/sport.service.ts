import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Sport } from '../models/sport.model';

@Injectable({ providedIn: 'root' })
export class SportService {
 
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getSport(id: number) {
    return this.http.get<Sport>(`${this.apiUrl}/sports/${id}`, { withCredentials: true });
  }
  getSports(): Observable<Sport[]> {
    return this.http.get<Sport[]>(`${this.apiUrl}/sports/`, { withCredentials: true });
  }

  delete(id: number) : Observable<Sport> {
      return this.http.delete<Sport>(`${this.apiUrl}/sports/${id}`, { withCredentials: true });
  }

  updateSport(editingId: number, sport: Sport) : Observable<Sport> {
      return this.http.put<Sport>(`${this.apiUrl}/sports/${editingId}`, sport,{ withCredentials : true });
  }

  createSport(sport: Sport) : Observable<Sport> {
      return this.http.post<Sport>(`${this.apiUrl}/sports/`, sport,{ withCredentials : true });
  }

}