import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { Club } from '../../models/club.model';
import { Sport } from '../../models/sport.model';
import { ClubService } from '../../service/club.service';
import { SportService } from '../../service/sport.service';

@Component({
  selector: 'app-club-filter',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule, MatCardModule
  ],
  templateUrl: './club-filter.component.html',
  styleUrls: ['./club-filter.component.scss']
})
export class ClubFilterComponent implements OnInit{
  @Output() filtersChanged = new EventEmitter<{ search?: string; sport?: string; city?: string }>();
  filterForm!: FormGroup;
  clubs: Club[] = [];
  sports: Sport[] = [];
  cities: string[] = [];
  loadingClubs = true;
  loadingSports = true;

  constructor(private fb: FormBuilder,private clubService: ClubService,
      private sportService: SportService,) {
    
  }
  ngOnInit(): void {
    this.filterForm = this.fb.group({
      search: [''],
      sport: [''],
      city: ['']
    });
    this.loadClubs();
    this.loadSports();
  }

  loadClubs(): void {
    this.clubService.getClubs().subscribe({
      next: (data: any) => {
        this.clubs = data.content ?? data;
        this.cities = Array.from(
            new Set(this.clubs.map((club) => club.city))); 
        this.loadingClubs = false;
      },
      error: (err) => {
        console.error('Error cargando clubes:', err);
        this.loadingClubs = false;
      }
    });
  }

  loadSports(): void {
    this.sportService.getSports().subscribe({
      next: (data: any) => {
        this.sports = data.content ?? data;
        this.loadingSports = false;
      },
      error: (err) => {
        console.error('Error cargando deportes:', err);
        this.loadingSports = false;
      }
    });
  }

  onFilter(): void {
    this.filtersChanged.emit(this.filterForm.value);
  }
}
