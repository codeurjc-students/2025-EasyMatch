import { Component, inject, Input, signal } from '@angular/core';
import { Match } from '../../models/match.model';
import { CommonModule, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { environment } from '../../../environments/environment';


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
export class MatchComponent {
  @Input() match!: Match;
  private apiUrl = environment.apiUrl;
  
  getUserImage(id: number): string {
    return `${this.apiUrl}/users/${id}/image`;
  }

  getTotalPlayers(): number{
    return (this.match.team1Players.length + this.match.team2Players.length);
  }

}
  



