import { Injectable, numberAttribute } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Match } from '../models/match.model';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class MatchService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getMatches(page = 0, size = 6, sort = 'date,asc'): Observable<{
    content: Match[];
    totalElements: number;
    totalPages: number;
    number: number;
  }> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);

    return this.http
      .get<{ content: Match[]; totalElements: number; totalPages: number; number: number }>(
        `${this.apiUrl}/matches/`,
        { params, withCredentials: true }
      )
      .pipe(
        map(response => ({
          ...response,
          content: response.content
        }))
      );
  }

  getUserImage(id: number): Observable<string> {
    const url = `${this.apiUrl}/users/${id}/image`;

    return this.http.get(url, {
      responseType: 'blob', 
      withCredentials: true, 
    }).pipe(
      map(blob => URL.createObjectURL(blob))
    );
  }

  getMatch(id: number): Observable<Match> {
    return this.http.get<Match>(`${this.apiUrl}/matches/${id}`,{withCredentials : true});
  }

}


