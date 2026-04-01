import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, map } from 'rxjs';

import { Client } from '@stomp/stompjs';
import { ChatMessage } from '../models/chat-message.model';
import SockJS from 'sockjs-client';

@Injectable({ providedIn: 'root' })
export class ChatService {

  private http = inject(HttpClient);
  private stompClient!: Client;
  private messageSubject = new BehaviorSubject<ChatMessage[]>([]);
  private currentMatchId: number | null = null;
  isConnected = signal(false);
  private isConnecting = false;

  constructor() {
    this.initConnectionSocket();
  }

  private initConnectionSocket() {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000
    });

    this.stompClient.onConnect = () => {
      this.isConnected.set(true);
      this.isConnecting = false;

      if (this.currentMatchId !== null) {
        this.subscribeToMatch(this.currentMatchId);
      }
    };

    this.stompClient.onStompError = (frame: any) => {
      console.error('STOMP error:', frame);
    };
  }

  private subscribeToMatch(matchId: number) {
    this.stompClient.subscribe(`/topic/match/${matchId}`, (message: any) => {

      const parsed: ChatMessage = JSON.parse(message.body);

      const current = this.messageSubject.getValue();

      this.messageSubject.next([...current, parsed]);
    });
  }

  joinMatchChat(matchId: number) {

    if (this.currentMatchId === matchId) return;

    this.currentMatchId = matchId;

    if (!this.isConnected() && !this.isConnecting) {
      this.isConnecting = true;
      this.stompClient.activate();
    }

    if (this.isConnected()) {
      this.subscribeToMatch(matchId);
    }

    this.loadMessages(matchId);
  }

  sendMessage(matchId: number, message: ChatMessage) {

    if (!this.isConnected()) {
      console.error('STOMP not connected');
      return;
    }

    this.stompClient.publish({
      destination: `/app/chat/${matchId}`,
      body: JSON.stringify(message)
    });
  }

  getMessages() {
    return this.messageSubject.asObservable();
  }

  private loadMessages(matchId: number) {
    this.http.get<ChatMessage[]>(`/api/v1/matches/${matchId}/messages`, {
      withCredentials: true
    })
    .pipe(
      map(res => res.map(m => ({
        matchId: m.matchId,
        content: m.content,
        senderUsername: m.senderUsername,
        type: m.type,
        timestamp: m.timestamp
      }) as ChatMessage))
    )
    .subscribe({
      next: messages => this.messageSubject.next(messages),
      error: err => console.error(err)
    });
  }

  getUserChats(userId: number) {
    
    return this.http.get<ChatMessage[]>(
      `/api/v1/users/${userId}/messages/`,
      { withCredentials: true }
    );
  }

}