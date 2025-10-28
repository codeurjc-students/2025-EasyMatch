import { Component, inject, Input, signal } from '@angular/core';
import { Match } from '../../models/match.model';
import { CommonModule, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';


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
  getUserImage(id: number): string {
    return `https://localhost:8443/api/v1/users/${id}/image`;
  }
  
  
}
  



