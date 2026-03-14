import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { Club } from '../../models/club.model';
import { MatCard } from "@angular/material/card";
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: 'app-club-dialog',
  imports: [CommonModule, MatDialogModule, MatCard, MatIcon],
  templateUrl: './club-dialog.component.html',
  styleUrls: ['./club-dialog.component.scss'],
})
export class ClubDialogComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA) public club: Club
  ) {}
  getSportIcon(): string {
    const sport = this.club.sports?.[0]?.name?.toLowerCase();
    switch (sport) {
      case 'tenis':
      case 'tennis':
        return 'sports_tennis';

      case 'padel':
        return 'sports_tennis';

      case 'futbol':
      case 'football':
        return 'sports_soccer';

      case 'baloncesto':
      case 'basket':
        return 'sports_basketball';

      case 'voleibol':
        return 'sports_volleyball';

      default:
        return 'sports';
    }
  }
}