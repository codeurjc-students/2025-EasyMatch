import { Component, inject, Input, OnInit, signal } from '@angular/core';
import { Match } from '../../models/match.model';
import { CommonModule, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { environment } from '../../../environments/environment';
import { MatDialog } from '@angular/material/dialog';
import { MatchService } from '../../service/match.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { JoinMatchDialogComponent } from '../join-match-dialog/join-match-dialog.component';
import { UserService } from '../../service/user.service';
import { User } from '../../models/user.model';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';


@Component({
  selector: 'app-match',
  standalone: true,
  templateUrl: './match.html',
  styleUrls: ['./match.scss'],
  imports: [
    CommonModule,
    DatePipe,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule,
    MatTooltipModule,
  ]
})
export class MatchComponent implements OnInit{

  @Input() match!: Match;
  private apiUrl = environment.apiUrl;
  currentUserId: number = 0;

  constructor(private dialog: MatDialog, private service: MatchService, private userService: UserService, private snack: MatSnackBar) {}
  ngOnInit(): void {
     this.userService.getCurrentUser().subscribe( (user: User) => {
      this.currentUserId = user.id;
    });
    
  }

  
  getUserImage(id: number): string {
    return `${this.apiUrl}/users/${id}/image`;
  }

  getTotalPlayers(): number{
    return (this.match.team1Players.length + this.match.team2Players.length);
  }

  

  openJoinDialog(match : Match) {
    const dialogRef = this.dialog.open(JoinMatchDialogComponent, {
      data: { match },
      width: '450px'
    });

    dialogRef.afterClosed().subscribe(team => {
      if (team) {
        this.service.joinMatch(this.match.id!, team).subscribe({
          next: () => {
            this.snack.open('✅ ¡Te has unido al partido!', 'Cerrar', { duration: 3000 });
            this.reloadAfterSave();
          },
          error: (err) => {
            console.error('Error al unirse:', err);
            this.snack.open('❌ Error al unirse al partido', 'Cerrar', { duration: 3000 });
          }
        });
      }
    });
  }
  leaveMatch(matchId: number) {
    this.service.leaveMatch(matchId).subscribe({
      next: () => {
        this.snack.open('✅ Has abandado el partido correctamente', 'Cerrar', { duration: 3000 });
        this.reloadAfterSave();
      },
      error: (err) => {
        console.error("Error al abandonar el partido:", err)
        this.snack.open('❌ Error al abandonar el partido', 'Cerrar', {
            duration: 4000,
            panelClass: ['error-snackbar'],
          });
      }
    });
  }
  openLeaveDialog(match: Match) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Abandonar partido',
        message: '¿Seguro que quieres abandonar este partido?',
        confirmText: 'Abandonar',
        cancelText: 'Cancelar'
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.leaveMatch(match.id!);
      }
    });
  }
  addResult(match: Match) {
    console.log("Añadir resultado para partido", match.id);
  }
  editResult(match: Match) {
    throw new Error('Method not implemented.');
  }
  hasResult(match: Match): boolean {
    if (!match.result) return false;

    const hasScores = 
      match.result.team1Score != null && 
      match.result.team2Score != null;

    const hasSets = 
      (match.result.team1Sets ?? 0) > 0 || 
      (match.result.team2Sets ?? 0) > 0;

    const hasGames =
      (match.result.team1GamesPerSet?.length ?? 0) > 0 ||
      (match.result.team2GamesPerSet?.length ?? 0) > 0;

    
    return hasScores || hasSets || hasGames;
  }

  isUserJoined(match: Match, userId: number): boolean {
    const inTeam1 = match.team1Players.some(p => p.id == userId);
    const inTeam2 = match.team2Players.some(p => p.id == userId);
    return inTeam1 || inTeam2;
  }

  private reloadAfterSave() {
    window.location.reload();
  }

}
  



