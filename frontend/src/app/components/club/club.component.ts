import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Club } from '../../models/club.model';

@Component({
  selector: 'app-club',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './club.component.html',
  styleUrls: ['./club.component.scss'],
})
export class ClubComponent {
  @Input() club!: Club;
  getClubImage(id: number): string {
    return `https://localhost:8443/api/v1/clubs/${id}/image`;
  }
}
