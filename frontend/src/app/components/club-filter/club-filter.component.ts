import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

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
export class ClubFilterComponent {
  @Output() filtersChanged = new EventEmitter<{ search?: string; sport?: string; city?: string }>();

  filterForm: FormGroup;

  sports = ['Pádel', 'Tenis', 'Fútbol', 'Volley'];
  cities = ['Madrid', 'Valencia', 'Barcelona'];

  constructor(private fb: FormBuilder) {
    this.filterForm = this.fb.group({
      search: [''],
      sport: [''],
      city: ['']
    });
  }

  onFilter(): void {
    this.filtersChanged.emit(this.filterForm.value);
  }
}
