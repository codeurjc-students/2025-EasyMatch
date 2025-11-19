import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UserService } from '../../service/user.service';
import { User } from '../../models/user.model';
import { HeaderComponent } from "../header/header.component";
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { environment } from '../../../environments/environment';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatDividerModule, HeaderComponent],
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  user = signal<User | null>(null);
  loading = signal(true);
  private apiUrl = environment.apiUrl;

  constructor(private userService: UserService, private dialog: MatDialog) {}

  
  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (data) => {
        this.user.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando usuario:', err);
        this.loading.set(false);
      }
    });
  }

  getUserImage(id: number): string {
    return `${this.apiUrl}/users/${id}/image`;
  }

  onEditProfile(): void {
    console.log('Funcionalidad de editar perfil pendiente de implementar.');
  }

  onDeleteAccount(id: number): void {

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Eliminar cuenta',
        message:
          '¿Estás seguro de que quieres eliminar tu cuenta? Esta acción es permanente y no se puede deshacer.',
        confirmText: 'Eliminar cuenta',
        cancelText: 'Cancelar'
      }
    });
    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.userService.deleteUser(id).subscribe({
          next: () => {
            alert('Tu cuenta ha sido eliminada correctamente.');
            window.location.href = '/';
          },
          error: (err) => {
            console.error('Error al eliminar cuenta:', err);
            alert('Error al eliminar la cuenta. Inténtalo de nuevo.');
          }
        });
      }
    });
  }
}

