import { Component, Input } from '@angular/core';
import { Match } from '../../models/match.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-match',
  templateUrl: './match.html',
  imports: [CommonModule],
  standalone: true,
})
export class MatchComponent {
  @Input() match!: Match;
}
