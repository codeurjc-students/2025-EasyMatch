// Angular core
import { Component, OnInit, ChangeDetectionStrategy, inject, signal } from '@angular/core';

// Forms
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

// Angular common & material
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';

// Routing
import { ActivatedRoute, Router } from '@angular/router';

// Services & models
import { MessageService } from '../../../service/message.service';
import { ChatMessage } from '../../../models/chat-message.model';

@Component({
  selector: 'app-admin-message-create',
  templateUrl: './admin-message-create.component.html',
  styleUrls: ['../admin-entity-create.component.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'admin-message-edit' }
})
export default class AdminMessageCreateComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly messageService = inject(MessageService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  readonly editingId = signal<number | null>(null);
  readonly loading = signal(true);

  originalMessage!: ChatMessage;

  readonly form = this.fb.nonNullable.group({
    content: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(500)]]
  });

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const id = Number(params['id']);
      if (!id) return;

      this.editingId.set(id);
      this.loadMessage(id);
    });
  }

  private loadMessage(id: number): void {
    this.messageService.getMessage(id).subscribe({
      next: (m: ChatMessage) => {
        this.originalMessage = m;

        this.form.patchValue({
          content: m.content
        });

        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando mensaje', err);
        this.snackBar.open('❌ Error cargando mensaje', 'Cerrar', { duration: 4000 });
        this.router.navigate(['/admin/messages']);
      }
    });
  }

  save(): void {
    if (this.form.invalid || !this.editingId()) return;

    const updated: ChatMessage = {
      ...this.originalMessage,
      content: this.form.getRawValue().content,
      timestamp: this.getLocalISOString()
    };

    this.messageService.updatechatMessage(this.editingId()!, updated).subscribe({
      next: () => {
        this.snackBar.open('✅ Mensaje actualizado correctamente', 'Cerrar', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });

        this.router.navigate(['/admin/messages']);
      },
      error: (err) => {
        console.error('Error actualizando mensaje', err);

        this.snackBar.open('❌ Error al actualizar mensaje', 'Cerrar', {
          duration: 4000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  private getLocalISOString(): string {
    const date = new Date();
    const offset = date.getTimezoneOffset() * 60000;
    const localDate = new Date(date.getTime() - offset);
    return localDate.toISOString().slice(0, -1);
  }

  cancel(): void {
    this.router.navigate(['/admin/messages']);
  }
}