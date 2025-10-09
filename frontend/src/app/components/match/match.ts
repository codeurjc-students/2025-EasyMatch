import { Component, Input } from '@angular/core';
import { Match } from '../../models/match.model';

@Component({
  selector: 'app-match',
  templateUrl: './match.html',
  standalone: false,
})
export class MatchComponent {
  @Input() match!: Match;
}
