import { Injectable} from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Match } from '../models/match.model';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { JoinMatchResponse } from '../models/join-match-response';

@Injectable({ providedIn: 'root' })
export class MatchService {
  
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getMatches(page = 0, 
    size = 10, 
    sort : string,
    filters?: { search?: string; sport?: string; timeRange?: string, includeFriendlies?: boolean }
  ): Observable<{
    content: Match[];
    totalElements: number;
    totalPages: number;
    number: number;
  }> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);

    if (filters?.search) params = params.set('search', filters.search);
    if (filters?.sport) params = params.set('sport', filters.sport);
    if (filters?.timeRange) params = params.set('timeRange', filters.timeRange);
    if (filters?.includeFriendlies !== undefined) params = params.set('includeFriendlies', String(filters.includeFriendlies));

    return this.http
      .get<{ content: Match[]; totalElements: number; totalPages: number; number: number }>(
        `${this.apiUrl}/matches`,
        { params, withCredentials: true }
      ).pipe(map(response => ({ ...response, content: response.content })));
  }


  getMatch(id: number): Observable<Match> {
    return this.http.get<Match>(`${this.apiUrl}/matches/${id}`,{withCredentials : true});
  }

  createMatch(matchData: Partial<Match>): Observable<Match> {
    const payload = {
      ...matchData,
      date: matchData.date instanceof Date ? matchData.date.toISOString() : matchData.date
    };

    return this.http
      .post<Match>(`${this.apiUrl}/matches`, payload, { withCredentials: true })
      .pipe(
        map(response => ({
          ...response,
          date: new Date(response.date) 
        }))
      );
  }

  joinMatch(id: number, team: string): Observable<JoinMatchResponse> {
    return this.http.put<JoinMatchResponse>(`${this.apiUrl}/matches/${id}`, { team }, { withCredentials: true });
  }
  
  leaveMatch(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/matches/${id}`, { withCredentials: true });
  }
  

}


