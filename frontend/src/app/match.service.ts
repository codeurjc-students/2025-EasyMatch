import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Match } from './match.model';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class MatchService {

  private baseUrl = '/api/matches/'; 

  constructor(private http: HttpClient) {}

  getMatches(): Observable<Match[]> {
    return this.http.get<any>(this.baseUrl).pipe(
    map(response => Array.isArray(response) ? response : (response && response.content ? response.content : [])));
  }
}


