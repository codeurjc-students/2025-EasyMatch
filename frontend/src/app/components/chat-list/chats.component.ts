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
  private user = toSignal(this.loginService.currentUser$, { initialValue: null });

  chats = signal<ChatMessage[]>([]);
  visibleChats = signal<ChatMessage[]>([]);
  visibleCount = signal(5);

  constructor() {
    this.loadChats();
  }

  loadChats() {
    this.chatService.getUserChats(this.user()?.id!)
      .subscribe(chats => {
        const lastMessages = this.mapToLastMessages(chats);
        this.chats.set(lastMessages);
        this.updateVisibleChats();
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

  // Updates visible chats list according to current limit
  private updateVisibleChats(): void {
    this.visibleChats.set(
      this.chats().slice(0, this.visibleCount())
    );
  }

  // Loads 5 more chats
  loadMore(): void {
    this.visibleCount.update(value => value + 5);

    this.updateVisibleChats();
  }

  // Indicates if there are more chats to be shown
  hasMoreChats(): boolean {
    return this.visibleChats().length < this.chats().length;
  }
}