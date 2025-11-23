import { Component, Inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogActions, MatDialogContent } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { Match } from '../../models/match.model';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-join-match-dialog',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatSelectModule, MatCardModule, MatIconModule, MatDividerModule, MatDialogActions, MatDialogContent],
  templateUrl: './join-match-dialog.component.html',
  styleUrls: ['./join-match-dialog.component.scss']
})
export class JoinMatchDialogComponent {
  selectedTeam = signal<string | null>(null);
  team1Players: User[] = [];
  team2Players: User[] = [];
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { match: Match },
    private dialogRef: MatDialogRef<JoinMatchDialogComponent>
  ) {}

  get teamAPlayers(): User[] {
    this.team1Players = this.data.match.team1Players
    return this.team1Players;
  }

  get teamBPlayers(): User[] {
    this.team2Players = this.data.match.team2Players
    return this.team2Players;
  }

  get maxPlayers(): number{
    return this.data.match.sport.modes[0].playersPerGame / 2;
  }


  onConfirm() {
    if (this.selectedTeam()) this.dialogRef.close(this.selectedTeam());
  }

  onCancel() {
    this.dialogRef.close();
  }
}
