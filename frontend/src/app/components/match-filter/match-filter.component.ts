import { Component, Output, EventEmitter, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { A11yModule } from "@angular/cdk/a11y";
import { SportService } from '../../service/sport.service';
import { Sport } from '../../models/sport.model';

@Component({
  selector: 'app-match-filter',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatButtonModule,
    MatIconModule,
    A11yModule
],
  templateUrl: './match-filter.component.html',
  styleUrls: ['./match-filter.component.scss'],
})
export class MatchFilterComponent implements OnInit {
  @Output() filtersChanged = new EventEmitter<{ search?: string; sport?: string; timeRange?: string, includeFriendlies: boolean }>();
  filterForm!: FormGroup;
  sports: Sport[] = [];
  loadingSports = true;
  
  constructor(private fb: FormBuilder, private sportService: SportService,) {
  };
    

  ngOnInit(): void {
    this.filterForm = this.fb.group({
      search: [''],
      sport:[''],
      includeFriendlies: [true],
      timeRange: [''],
    });
    this.loadSports();
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

  toggleFriendlies(): void {
    const control = this.filterForm.get('includeFriendlies');
    control?.setValue(!control.value);
    this.filtersChanged.emit({ ...this.filterForm.value });
  }
}