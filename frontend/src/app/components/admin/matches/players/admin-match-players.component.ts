import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatchService } from '../../../../service/match.service';
import { UserService } from '../../../../service/user.service';
import { ConfirmDialogComponent } from '../../../confirm-dialog/confirm-dialog.component';
import { User } from '../../../../models/user.model';
import { MatSelect, MatSelectModule } from '@angular/material/select';

@Component({
  standalone: true,
  selector: 'app-admin-match-players',
  templateUrl: './admin-match-players.component.html',
  styleUrls: ['../../admin-entity.component.scss'],
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, RouterModule, MatSelectModule],
})
export class AdminMatchPlayersComponent implements OnInit {

  private matchService = inject(MatchService);
  private userService = inject(UserService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  matchId!: number;
  team1Players: any[] = [];
  team2Players: any[] = [];
  selectedPlayerTeam1?: number;
  selectedPlayerTeam2?: number;
  allUsers: User[] = [];

  displayedColumns = ['id', 'name', 'actions'];

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.matchId = +params['matchId'];
      this.loadPlayers();
      this.userService.getAllUsers().subscribe(response => {
        this.allUsers = response.content;
      });
    });
  }

  loadPlayers(): void {
    this.matchService.getTeam1Players(this.matchId).subscribe(players => this.team1Players = players);
    this.matchService.getTeam2Players(this.matchId).subscribe(players => this.team2Players = players);
  }

  addPlayer(team: 1 | 2, playerId?: number): void {
    if (!playerId) return;

    if (team === 1) {
      this.matchService.addPlayerToTeam1(this.matchId, playerId)
        .subscribe(() => {
          this.selectedPlayerTeam1 = undefined;
          this.afterAction('Equipo 1');
        });
    } else {
      this.matchService.addPlayerToTeam2(this.matchId, playerId)
        .subscribe(() => {
          this.selectedPlayerTeam2 = undefined;
          this.afterAction('Equipo 2');
        });
    }
  }
  removePlayer(team: 1 | 2, playerId: number): void {
    const teamName = team === 1 ? 'Equipo 1' : 'Equipo 2';
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Eliminar jugador',
        message: `¿Eliminar jugador del ${teamName}?`,
        confirmText: 'Eliminar',
        cancelText: 'Cancelar'
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        if (team === 1) {
          this.matchService.removePlayerFromTeam1(this.matchId, playerId).subscribe(() => this.afterAction(teamName));
        } else {
          this.matchService.removePlayerFromTeam2(this.matchId, playerId).subscribe(() => this.afterAction(teamName));
        }
      }
    });
  }

  private afterAction(teamName: string) {
    this.snackBar.open(`✅ Acción realizada en ${teamName}`, 'Cerrar', { duration: 3000, panelClass: ['success-snackbar'] });
    this.loadPlayers();
  }

  get availableUsers(): any[] {
    const assignedIds = [
      ...this.team1Players.map(p => p.id),
      ...this.team2Players.map(p => p.id)
    ];
    return this.allUsers.filter(u => !assignedIds.includes(u.id));
  }

  backToMatch() {
    this.router.navigate(['/admin/matches']);
  }
}