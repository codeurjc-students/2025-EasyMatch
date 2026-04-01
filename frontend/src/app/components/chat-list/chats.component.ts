import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';

import { ChatMessage } from '../../models/chat-message.model';
import { ChatService } from '../../service/chat-message.service';
import { HeaderComponent } from "../header/header.component";
import { DatePipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { toSignal } from '@angular/core/rxjs-interop';
import { LoginService } from '../../service/login.service';

@Component({
  selector: 'app-chats',
  templateUrl: './chats.component.html',
  styleUrl: './chats.component.scss',
  imports: [MatListModule, MatCardModule, HeaderComponent,DatePipe, MatIconModule],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChatsComponent {

  private chatService = inject(ChatService);
  private loginService = inject(LoginService);
  private router = inject(Router);

  chats = signal<ChatMessage[]>([]);
  private user = toSignal(this.loginService.currentUser$, { initialValue: null });


  constructor() {
    this.loadChats();
  }

  loadChats() {
    this.chatService.getUserChats(this.user()?.id!)
      .subscribe(chats => {
        const lastMessages = this.mapToLastMessages(chats);
        this.chats.set(lastMessages);
    });
  }

  openChat(matchId: number) {
    this.router.navigate(['/chat', matchId]);
  }

  private mapToLastMessages(messages: ChatMessage[]): ChatMessage[] {
    const map = new Map<number, ChatMessage>();

    for (const msg of messages) {

      const current = map.get(msg.matchId);

      if (!current || new Date(msg.timestamp) > new Date(current.timestamp)) {
        map.set(msg.matchId, msg);
      }
    }

    return Array.from(map.values())
      .sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
  }
}