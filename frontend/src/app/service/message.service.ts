import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { ChatMessage } from "../models/chat-message.model";
import { map, Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class MessageService {
  
  
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}
  
  getMessages(page = 0, 
    size = 10, 
    sort = 'id,asc'
  ): Observable<{
    content: ChatMessage[];
    totalElements: number;
    totalPages: number;
    number: number;
  }> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);

    return this.http
      .get<{ content: ChatMessage[]; totalElements: number; totalPages: number; number: number }>(
        `${this.apiUrl}/messages`,
        { params }
      ).pipe(map(response => ({ ...response, content: response.content })));
  }

  getMessage(id: number): Observable<ChatMessage> {
    return this.http.get<ChatMessage>(`${this.apiUrl}/messages/${id}`);
  }

  deleteMessage(id: number): Observable<ChatMessage> {
    return this.http.delete<ChatMessage>(`${this.apiUrl}/messages/${id}`);
  }
  
  updatechatMessage(editingId: number, payload: Partial<ChatMessage>): Observable<ChatMessage> {
    return this.http.put<ChatMessage>(`${this.apiUrl}/messages/${editingId}`, payload);
  }

}