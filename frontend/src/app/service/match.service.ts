import { Injectable} from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Match } from '../models/match.model';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { JoinMatchResponse } from '../models/join-match-response';
import { MatchResult } from '../models/match-result.model';
import { User } from '../models/user.model';

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

  getAllMatches(page = 0, size = 10): Observable<{
    content: Match[];
    totalElements: number;
    totalPages: number;
    number: number;
  }>{
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', 'id,asc');
    return this.http
      .get<{ content: Match[]; totalElements: number; totalPages: number; number: number }>(
        `${this.apiUrl}/matches/`,
        { params, withCredentials: true }
      ).pipe(map(response => ({ ...response, content: response.content })));

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
    return this.http.put<JoinMatchResponse>(`${this.apiUrl}/matches/${id}/users/me`, { team }, { withCredentials: true });
  }
  
  leaveMatch(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/matches/${id}/users/me`, { withCredentials: true });
  }

  deleteMatch(id: number): Observable<Match> {
      return this.http.delete<Match>(`${this.apiUrl}/matches/${id}`, { withCredentials: true });
  }

  updateMatch(editingId: number, matchData: Partial<Match>) : Observable<Match> {
      return this.http.put<Match>(`${this.apiUrl}/matches/${editingId}`, matchData, { withCredentials: true });
  }

  addMatchResult(matchId: number, result: MatchResult): Observable<MatchResult> {
    return this.http.put<MatchResult>(`${this.apiUrl}/matches/${matchId}/result`, result,{ withCredentials: true });
  }

  getTeam1Players(matchId: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/matches/${matchId}/team1Players`);
  }

  getTeam2Players(matchId: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/matches/${matchId}/team2Players`);
  }

  addPlayerToTeam1(matchId: number, userId: number) {
    return this.http.post(`${this.apiUrl}/matches/${matchId}/team1Players/${userId}`, { userId });
  }

  addPlayerToTeam2(matchId: number, userId: number) {
    return this.http.post(`${this.apiUrl}/matches/${matchId}/team2Players/${userId}`, { userId });
  }

  removePlayerFromTeam1(matchId: number, userId: number) {
    return this.http.delete(`${this.apiUrl}/matches/${matchId}/team1Players/${userId}`);
  }

  removePlayerFromTeam2(matchId: number, userId: number) {
    return this.http.delete(`${this.apiUrl}/matches/${matchId}/team2Players/${userId}`);
  }
  
}