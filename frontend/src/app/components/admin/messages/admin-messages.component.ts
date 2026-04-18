// Angular core
import { Component, OnInit, ChangeDetectionStrategy, inject, signal } from '@angular/core';

// Angular common & material
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

// Routing
import { Router } from '@angular/router';

// Shared components
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';
import { MessageService } from '../../../service/message.service';
import { ChatMessage } from '../../../models/chat-message.model';


@Component({
  selector: 'app-admin-messages',
  templateUrl: './admin-messages.component.html',
  styleUrls: ['../admin-entity.component.scss'],
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatPaginator],
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'admin-messages' }
})
export default class AdminMessagesComponent implements OnInit {

  // Services
  private readonly messageService = inject(MessageService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  readonly totalElements = signal(0);
  readonly messages = signal<ChatMessage[]>([]);


  pageSize = 10;
  pageIndex = 0;

  // Table config
  readonly displayedColumns = ['id', 'sender', 'type', 'content', 'date', 'actions'];

  ngOnInit(): void {
    this.loadMessages(this.pageIndex, this.pageSize);
  }


  loadMessages(page: number, size: number): void {
    this.messageService.getMessages(page, size).subscribe({
      next: (data) => {
        this.messages.set(data.content);
        this.pageIndex = data.number;
        this.totalElements.set(data.totalElements);
      }
    });
  }


  editMessage(id: number): void {
    this.router.navigate(['/admin/messages/create'], { queryParams: { id } });
  }

  createMessage(): void {
    this.router.navigate(['/admin/messages/create']);
  }


  deleteMessage(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Eliminar mensaje',
        message: '¿Seguro que quieres eliminar este mensaje? Esta acción es irreversible.',
        confirmText: 'Eliminar',
        cancelText: 'Cancelar'
      }
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;

      this.messageService.deleteMessage(id).subscribe({
        next: () => {
          this.snackBar.open('✅ Mensaje eliminado', 'Cerrar', {
            duration: 3000,
            panelClass: ['success-snackbar'],
          });

          this.loadMessages(this.pageIndex, this.pageSize);
        },
        error: (err) => {
          console.error('Error al eliminar mensaje:', err);

          this.snackBar.open('❌ Error al eliminar mensaje', 'Cerrar', {
            duration: 4000,
            panelClass: ['error-snackbar'],
          });
        }
      });
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.loadMessages(this.pageIndex, this.pageSize);
  }
}