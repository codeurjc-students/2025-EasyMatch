import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Sport } from '../models/sport.model';

@Injectable({ providedIn: 'root' })
export class SportService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getSports(): Observable<Sport[]> {
    return this.http.get<Sport[]>(`${this.apiUrl}/sports/`, { withCredentials: true });
  }
}