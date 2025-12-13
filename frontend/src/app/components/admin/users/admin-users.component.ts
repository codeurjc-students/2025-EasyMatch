import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { User } from '../../../models/user.model';
import { UserService } from '../../../service/user.service';
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  standalone: true,
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrls: ['.././admin-entity.component.scss'],
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatPaginator],
})
export class AdminUsersComponent implements OnInit {

  private userService = inject(UserService);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  totalElements = signal(0);
  pageSize = 10;
  pageIndex = 0;

  users: User[] = [];
  displayedColumns = ['id', 'realname', 'username','gender', 'email', 'level','description', 'actions'];

  ngOnInit(): void {
    this.loadUsers(this.pageIndex, this.pageSize);
  }

  loadUsers(page : number, size: number): void {
    this.userService.getAllUsers(page,size).subscribe((data) => {
      this.users = data.content;
      this.pageIndex = data.number;
      this.totalElements.set(data.totalElements);
    });
  }

  editUser(id: number) {
    this.router.navigate(['/admin/users/create'], { queryParams: { id } });
  }

  createUser() {
    this.router.navigate(['/admin/users/create']);
  }

  deleteUser(id: number) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Eliminar cuenta',
        message:
          '¿Estás seguro de que quieres eliminar esta cuenta? Esta acción es permanente y no se puede deshacer.',
        confirmText: 'Eliminar cuenta',
        cancelText: 'Cancelar'
      }
    });
    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.userService.deleteUser(id).subscribe({
          next: () => {
            this.snackBar.open('✅ Cuenta eliminada correctamente', 'Cerrar', {
                duration: 3000,
                panelClass: ['success-snackbar'], 
            });
            window.location.href = '/admin/users';
          },
          error: (err) => {
            console.error('Error al eliminar cuenta:', err);
            this.snackBar.open('❌ Error al eliminar cuenta', 'Cerrar', {
            duration: 4000,
            panelClass: ['error-snackbar'],
          });
          }
        });
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.loadUsers(this.pageIndex, this.pageSize);
  }
}
