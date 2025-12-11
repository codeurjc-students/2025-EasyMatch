import { Component, OnInit, inject, signal, forwardRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SportService } from '../../../service/sport.service';
import { Sport } from '../../../models/sport.model';
import { ScoringTypeLabelPipe } from './scoring-type.label.component';

@Component({
  standalone: true,
  selector: 'app-admin-matches',
  templateUrl: './admin-sports.component.html',
  styleUrls: ['.././admin-entity.component.scss'],
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, ScoringTypeLabelPipe],
  
})

export class AdminSportsComponent implements OnInit {
  
  private sportService = inject(SportService);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  sports: Sport[] = [];
  displayedColumns = ['id', 'name', 'scoringType','modes','actions'];

  ngOnInit(): void {
    this.loadSports();
  }

  loadSports(): void {
    this.sportService.getSports().subscribe((data) => {
      console.log('SPORTS:', data);
      this.sports = data;
    });
  }

  editSport(id: number) {
    this.router.navigate(['/admin/sports/create'], { queryParams: { id } });
  }

  createSport() {
    this.router.navigate(['/admin/sports/create']);
  }

  deleteSport(id: number) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Eliminar deporte',
        message:
          '¿Estás seguro de que quieres eliminar este deporte? Esta acción es permanente y no se puede deshacer.',
        confirmText: 'Eliminar deporte',
        cancelText: 'Cancelar'
      }
    });
    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.sportService.delete(id).subscribe({
          next: () => {
            this.snackBar.open('✅ Deporte eliminado correctamente', 'Cerrar', {
                duration: 3000,
                panelClass: ['success-snackbar'], 
            });
            window.location.href = '/admin/sports';
          },
          error: (err) => {
            console.error('Error al eliminar deporte:', err);
            this.snackBar.open('❌ Error al eliminar deporte', 'Cerrar', {
            duration: 4000,
            panelClass: ['error-snackbar'],
          });
          }
        });
      }
    });
  }

}
