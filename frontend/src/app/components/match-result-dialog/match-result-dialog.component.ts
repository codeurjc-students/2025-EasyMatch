import { Component, Inject, signal, computed, inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogActions,
  MatDialogContent
} from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatInputModule } from '@angular/material/input';

import { Match } from '../../models/match.model';
import { ScoringType } from '../../models/scoring-type';


@Component({
  selector: 'app-match-result-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatDividerModule,
    MatInputModule,
    MatDialogActions,
    MatDialogContent
  ],
  templateUrl: './match-result-dialog.component.html',
  styleUrls: ['./match-result-dialog.component.scss']
})

export class MatchResultDialogComponent {
  constructor(
    private dialogRef: MatDialogRef<MatchResultDialogComponent>
  ) {}  

  private data = inject<{ readonly?:boolean, match: Match }>(MAT_DIALOG_DATA);

  isSets = computed(
    () => this.data.match.sport.scoringType === ScoringType.SETS
  );

  team1Score = signal<number>(this.data.match.result?.team1Score ?? 0);
  team2Score = signal<number>(this.data.match.result?.team2Score ?? 0);
  errorMessage = signal<string | null>(null);

  team1Sets = signal<number[]>(
    this.data.match.result?.team1GamesPerSet?.length
      ? [...this.data.match.result.team1GamesPerSet]
      : [0, 0, 0]
  );

  team2Sets = signal<number[]>(
    this.data.match.result?.team2GamesPerSet?.length
      ? [...this.data.match.result.team2GamesPerSet]
      : [0, 0, 0]
  );

  
  get isReadonly(): boolean {
    return !!this.data.readonly;
  }

  updateSet(team: 'A' | 'B', index: number, value: number) {
    const target = team === 'A' ? [...this.team1Sets()] : [...this.team2Sets()];
    target[index] = value;

    team === 'A'
      ? this.team1Sets.set(target)
      : this.team2Sets.set(target);
  }

  onConfirm() {
    
    if (this.isSets()) {
      let team1Sets = [...this.team1Sets()];
      let team2Sets = [...this.team2Sets()];
      const isValid = team1Sets.every(v => v >= 0 && v <= 7)
        && team2Sets.every(v => v >= 0 && v <= 7);

      if (!isValid) {
        this.errorMessage.set('Los juegos por set deben estar entre 0 y 7');
        return;
      }

      if (
        team1Sets.length > 1 &&
        team1Sets[team1Sets.length - 1] === 0 &&
        team2Sets[team2Sets.length - 1] === 0
      ) {
        team1Sets.pop();
        team2Sets.pop();
      }

      this.dialogRef.close({
        team1GamesPerSet: team1Sets,
        team2GamesPerSet: team2Sets
      });
      return;
    }
    if (this.team1Score() < 0 || this.team2Score() < 0) {
      this.errorMessage.set('Los puntos deben ser números positivos');
      return;
    }
    this.dialogRef.close({
        team1Score: this.team1Score(),
        team2Score: this.team2Score()
      } 
    );
  }

  onCancel() {
    this.dialogRef.close();
  }
}
