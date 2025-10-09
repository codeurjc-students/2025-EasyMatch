import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Match } from '../models/match.model';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class MatchService {

  private apiUrl = environment.apiUrl; 

  constructor(private http: HttpClient) {}

  getMatches(): Observable<Match[]> {
    return this.http.get<any>(`${this.apiUrl}`+'/matches/').pipe(
    map(response => Array.isArray(response) ? response : (response && response.content ? response.content : [])));
  }

  getMatch(id: number): Observable<Match> {
    return this.http.get<Match>(`${this.apiUrl}/${id}`);
  }
}


