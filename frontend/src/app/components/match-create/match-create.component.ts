import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIcon } from '@angular/material/icon';
import { Router } from '@angular/router';
import { MatchService } from '../../service/match.service';
import { ClubService } from '../../service/club.service';
import { Club } from '../../models/club.model';
import { Sport } from '../../models/sport.model';

@Component({
  selector: 'app-match-create',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSlideToggleModule,
    MatSnackBarModule,
    MatCardModule,
    MatIcon
  ],
  templateUrl: './match-create.component.html',
  styleUrls: ['./match-create.component.scss']
})
export class MatchCreateComponent implements OnInit {
  matchForm!: FormGroup;
  clubs: Club[] = [];
  sports: Sport[] = [];
  modes: { name: string; playersPerGame: number }[] = [];
  creating = false;
  loadingClubs = true;
  loadingSports = true;

  constructor(
    private fb: FormBuilder,
    private matchService: MatchService,
    private router: Router,
    private clubService: ClubService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.matchForm = this.fb.group({
      sport: ['', Validators.required],
      mode: [null, Validators.required],
      club: ['', Validators.required],
      price: [null, [Validators.required, Validators.min(0)]],
      date: [null, Validators.required],
      time: ['', Validators.required],
      type: [true, Validators.required], 
      isPrivate: [false, Validators.required]
    });
    this.loadClubs();
    this.matchForm.get('club')?.valueChanges.subscribe((selectedClub: Club | null) => {
      if (selectedClub && selectedClub.sports) {
        this.sports = selectedClub.sports;
        this.matchForm.get('sport')?.enable();
      }else{
        this.sports = [];
        this.matchForm.get('sport')?.disable();
      }
      this.matchForm.get('sport')?.reset();
    });
    this.matchForm.get('sport')?.valueChanges.subscribe((selectedSport: Sport | null) => {
      if (selectedSport && selectedSport.modes) {
        this.modes = selectedSport.modes;
        this.matchForm.get('mode')?.enable();
      } else {
        this.modes = [];
        this.matchForm.get('mode')?.disable();
      }
      this.matchForm.get('mode')?.reset();
    });
  }

  loadClubs(): void {
    this.clubService.getClubs().subscribe({
      next: (data: any) => {
        this.clubs = data.content ?? data;
        this.loadingClubs = false;
      },
      error: (err) => {
        console.error('Error cargando clubes:', err);
        this.loadingClubs = false;
      }
    });
  }


  onSubmit(): void {
    if (this.matchForm.invalid) return;

    const { date, time, ...rest } = this.matchForm.value;

    const [hours, minutes] = time.split(':').map(Number);
    const dateTime = new Date(date);
    dateTime.setHours(hours, minutes, 0, 0);

    const localISOString = new Date(dateTime.getTime() - dateTime.getTimezoneOffset() * 60000).toISOString();

    const matchData = { ...rest, date: localISOString, modeSelected: this.matchForm.value.sport.modes.indexOf(this.matchForm.value.mode) };

    this.creating = true;
    this.matchService.createMatch(matchData).subscribe({
        next: () => {
          this.creating = false;
          this.snackBar.open('✅ Partido creado correctamente', 'Cerrar', {
            duration: 3000,
            panelClass: ['success-snackbar'], 
          });
          setTimeout(() => this.router.navigate(['/matches']), 1000);
        },
        error: (err) => {
          console.error('Error creando partido:', err);
          this.creating = false;
          this.snackBar.open('❌ Error al crear el partido', 'Cerrar', {
            duration: 4000,
            panelClass: ['error-snackbar'],
          });
        }
    });
  }

  goBack(): void {
    this.router.navigate(['/matches']);
  }
}
