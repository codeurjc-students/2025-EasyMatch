import { Injectable, numberAttribute } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Club } from '../models/club.model';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ClubService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getClubs(
    page = 0,
    size = 10,
    sort = 'name,asc',
    filters?: { search?: string; city?: string; sport?: string }
  ): Observable<{
    content: Club[];
    totalElements: number;
    totalPages: number;
    number: number;
  }> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);

    if (filters?.search) params = params.set('search', filters.search);
    if (filters?.city) params = params.set('city', filters.city);
    if (filters?.sport) params = params.set('sport', filters.sport);

    return this.http
      .get<{ content: Club[]; totalElements: number; totalPages: number; number: number }>(
        `${this.apiUrl}/clubs`,
        { params, withCredentials: true }
      )
      .pipe(map(response => ({ ...response, content: response.content })));
  }


  getClub(id: number): Observable<Club> {
    return this.http.get<Club>(`${this.apiUrl}/clubs/${id}`,{withCredentials : true});
  }

}
