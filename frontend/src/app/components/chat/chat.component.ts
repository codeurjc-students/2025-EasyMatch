import {
  Component,
  ChangeDetectionStrategy,
  inject,
  signal,
  OnInit
} from '@angular/core';

import { ActivatedRoute } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

import { ChatService } from '../../service/chat-message.service';
import { ChatMessage } from '../../models/chat-message.model';
import { LoginService } from '../../service/login.service';
import { HeaderComponent } from "../header/header.component";
import { toSignal } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    HeaderComponent,
    MatIconModule
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChatComponent implements OnInit {

  private route = inject(ActivatedRoute);
  private chatService = inject(ChatService);
  private loginService = inject(LoginService);
  private fb = inject(FormBuilder);

  matchId = signal<number>(Number(this.route.snapshot.paramMap.get('id')));

  user = toSignal(this.loginService.currentUser$, { initialValue: null });

  messages = signal<any[]>([]);

  form = this.fb.group({
    content: ['']
  });

  private formatTime(date: string): string {
  const d = new Date(date);
  return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

ngOnInit(): void {
  const matchId = this.matchId();

  this.chatService.joinMatchChat(matchId);

  this.chatService.getMessages().subscribe(messages => {
    const currentUser = this.user()?.username;

      const mapped: ChatMessage[] = messages.map(msg => {
        const isSystem = msg.type === 'JOIN' || msg.type === 'LEAVE';

        return {
          ...msg,
          formattedTime: this.formatTime(msg.timestamp),
          message_side: isSystem
            ? 'system'
            : msg.senderUsername === currentUser
              ? 'sender'
              : 'receiver'
        };
      });

      this.messages.set(mapped);
    });
  }
  send() {
    const content = this.form.value.content;

    if (!content?.trim()) return;

    if (!this.chatService.isConnected()) return;

    this.chatService.sendMessage(this.matchId(), {
      matchId: this.matchId(),
      content,
      senderUsername: this.user()?.username!,
      type: 'CHAT',
      timestamp: new Date().toISOString()
    });

    this.form.reset();
  }
}