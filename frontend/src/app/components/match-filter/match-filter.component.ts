import { Component, Output, EventEmitter } from '@angular/core';
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
export class MatchFilterComponent {
  @Output() filtersChanged = new EventEmitter<{ search?: string; sport?: string; timeRange?: string, includeFriendlies: boolean }>();

  filterForm: FormGroup;
  
  sports = ['Pádel', 'Tenis', 'Fútbol', 'Volley'];
  cities = ['Madrid', 'Valencia', 'Barcelona'];
  
  constructor(private fb: FormBuilder) {
    this.filterForm = this.fb.group({
      search: [''],
      sport:[''],
      includeFriendlies: [false],
      timeRange: [''],
      
    });
    
}

  onFilter(): void {
    this.filtersChanged.emit(this.filterForm.value);
  }

  toggleFriendlies(): void {
    const current = this.filterForm.get('includeFriendlies')?.value;
    this.filterForm.get('includeFriendlies')?.setValue(!current);
    this.onFilter();
  }
}