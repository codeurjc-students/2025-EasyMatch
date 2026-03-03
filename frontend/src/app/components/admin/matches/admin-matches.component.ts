import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterModule } from '@angular/router';
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatchService } from '../../../service/match.service';
import { Match } from '../../../models/match.model';

@Component({
  standalone: true,
  selector: 'app-admin-matches',
  templateUrl: './admin-matches.component.html',
  styleUrls: ['.././admin-entity.component.scss'],
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatPaginator, RouterModule],
})
export class AdminMatchesComponent implements OnInit {

  private matchService = inject(MatchService);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  totalElements = signal(0);
  pageSize = 10;
  pageIndex = 0;

  matches: Match[] = [];
  displayedColumns = ['id', 'date', 'type', 'privacy', 'state', 'price', 'club', 'sport','organizer', 'players', 'actions',];

  ngOnInit(): void {
    this.loadMatches(this.pageIndex, this.pageSize);
  }

  loadMatches(page : number, size: number): void {
    this.matchService.getAllMatches(page,size).subscribe((data) => {
      this.matches = data.content;
      this.pageIndex = data.number;
      this.totalElements.set(data.totalElements);
    });
  }

  editMatch(id: number) {
    this.router.navigate(['/admin/matches/create'], { queryParams: { id } });
  }

  createMatch() {
    this.router.navigate(['/admin/matches/create']);
  }

  deleteMatch(id: number) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Eliminar partido',
        message:
          '¿Estás seguro de que quieres eliminar este partido? Esta acción es permanente y no se puede deshacer.',
        confirmText: 'Eliminar partido',
        cancelText: 'Cancelar'
      }
    });
    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.matchService.deleteMatch(id).subscribe({
          next: () => {
            this.snackBar.open('✅ Partido eliminado correctamente', 'Cerrar', {
                duration: 3000,
                panelClass: ['success-snackbar'], 
            });
            window.location.href = '/admin/matches';
          },
          error: (err) => {
            console.error('Error al eliminar partido:', err);
            this.snackBar.open('❌ Error al eliminar partido', 'Cerrar', {
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
    this.loadMatches(this.pageIndex, this.pageSize);
  }

  addPlayer(matchId: number) {
    this.router.navigate(['/admin/matches/players'], { queryParams: { matchId } });
  }

  removePlayer(matchId: number) {
    this.router.navigate(['/admin/matches/players'], { queryParams: { matchId, remove: true } });
  }
}
