import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ClubService } from '../../../service/club.service';
import { Club } from '../../../models/club.model';

@Component({
  standalone: true,
  selector: 'app-admin-clubs',
  templateUrl: './admin-clubs.component.html',
  styleUrls: ['.././admin-entity.component.scss'],
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatPaginator],
})
export class AdminClubsComponent implements OnInit {

  private clubService = inject(ClubService);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  totalElements = signal(0);
  pageSize = 10;
  pageIndex = 0;

  clubs: Club[] = [];
  displayedColumns = ['id', 'name', 'address', 'city', 'email', 'website', 'phone', 'actions',];

  ngOnInit(): void {
    this.loadClubs(this.pageIndex, this.pageSize);
  }

  loadClubs(page : number, size: number): void {
    this.clubService.getClubs(page,size).subscribe((data) => {
      this.clubs = data.content;
      this.pageIndex = data.number;
      this.totalElements.set(data.totalElements);
    });
  }

  editClub(id: number) {
    this.router.navigate(['/admin/clubs/create'], { queryParams: { id } });
  }

  createClub() {
    this.router.navigate(['/admin/clubs/create']);
  }

  deleteClub(id: number) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Eliminar club',
        message:
          '¿Estás seguro de que quieres eliminar este club? Esta acción es permanente y no se puede deshacer.',
        confirmText: 'Eliminar club',
        cancelText: 'Cancelar'
      }
    });
    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.clubService.deleteClub(id).subscribe({
          next: () => {
            this.snackBar.open('✅ Club eliminado correctamente', 'Cerrar', {
                duration: 3000,
                panelClass: ['success-snackbar'], 
            });
            window.location.href = '/admin/clubs';
          },
          error: (err) => {
            console.error('Error al eliminar club:', err);
            this.snackBar.open('❌ Error al eliminar club', 'Cerrar', {
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
    this.loadClubs(this.pageIndex, this.pageSize);
  }
}
