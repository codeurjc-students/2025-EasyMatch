import { Component, inject, Input, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Club } from '../../models/club.model';
import { environment } from '../../../environments/environment';
import { ClubDialogComponent } from '../club-dialog/club-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-club',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './club.component.html',
  styleUrls: ['./club.component.scss'],
})
export class ClubComponent {
  @Input() club!: Club;
  private apiUrl = environment.apiUrl;
  private dialog = inject(MatDialog);

  getClubImage(id: number): string {
    return `${this.apiUrl}/clubs/${id}/image`;
  }

  openDetails(): void {
    this.dialog.open(ClubDialogComponent, {
      width: '700px',
      height: 'auto',
      data: this.club
    });
  }
}
