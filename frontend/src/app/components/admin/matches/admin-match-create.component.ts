import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ClubService } from '../../../service/club.service';
import { Club } from '../../../models/club.model';
import { MatchService } from '../../../service/match.service';
import { Match } from '../../../models/match.model';
import { Sport } from '../../../models/sport.model';


@Component({
  selector: 'app-admin-club-create',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatDatepickerModule,
    FormsModule
],
  templateUrl: './admin-match-create.component.html',
  styleUrls: ['.././admin-entity-create.component.scss']
})
export class AdminMatchCreateComponent implements OnInit {

  private fb = inject(FormBuilder);
  private matchService = inject(MatchService);
  private clubService = inject(ClubService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);
  form!: FormGroup;
  editingId: number | null = null;
  clubs: Club[] = [];
  sports: Sport[] = [];
  modes: {name: string, playersPerGame: number}[] = [];
  loadingClubs = true;
  loadingSports = true;

  ngOnInit(): void {
    this.form = this.fb.group({
      sport: ['', Validators.required],
      mode: ['', Validators.required],
      club: ['', Validators.required],
      price: [null, [Validators.required, Validators.min(0)]],
      date: [null, Validators.required],
      time: ['', Validators.required],
      type: [true, Validators.required], 
      isPrivate: [false, Validators.required],
    });
    this.route.queryParams.subscribe(params => {
      if (params['id']) {
        this.editingId = +params['id'];
        this.loadMatch(+params['id']);
      }
    });
    
    this.loadClubs();
    this.setupListeners();
    
  };

  loadClubs(): void {
    this.clubService.getClubs().subscribe({
      next: (data: any) => {
        this.clubs = data.content ?? data;
        this.loadingClubs = false;
        if (this.editingId) {
            this.loadMatch(this.editingId);
        }
      },
      error: (err) => {
        console.error('Error cargando clubes:', err);
        this.loadingClubs = false;
      }
    });
  }

  loadMatch(id: number) {
    this.matchService.getMatch(id).subscribe({
        next: (m: Match) => {
            const dateTime = new Date(m.date);
            const hours = dateTime.getHours();
            const minutes = dateTime.getMinutes();
            dateTime.setHours(hours, minutes, 0, 0);

            const localISOString = new Date(dateTime.getTime() - dateTime.getTimezoneOffset() * 60000).toISOString();

            const timeString = localISOString.substring(11, 16);

            const selectedClub = this.clubs.find(c => c.id === m.club.id) ?? null;
            const selectedSport = selectedClub?.sports.find(s => s.id === m.sport.id) ?? null;
            const selectedMode = selectedSport?.modes.find(md =>
                md.name === m.sport.modes[0].name &&
                md.playersPerGame === m.sport.modes[0].playersPerGame
                ) ?? null;
           
            this.sports = selectedClub?.sports ?? [];    
            this.modes = selectedSport?.modes ?? [];

            this.form.get('sport')?.enable();
            this.form.get('mode')?.enable();
            
            this.form.patchValue({
                date: dateTime,
                time: timeString,
                type: m.type,
                isPrivate: m.isPrivate,
                price: m.price,
                club: selectedClub,
                sport: selectedSport,
                mode: selectedMode ,
            });
            

        }
    });
  }

   save() {
    if (this.form.invalid) return;
    const { date, time, ...rest } = this.form.value;

    const [hours, minutes] = time.split(':').map(Number);
    const dateTime = new Date(date);
    dateTime.setHours(hours, minutes, 0, 0);

    const localISOString = new Date(dateTime.getTime() - dateTime.getTimezoneOffset() * 60000).toISOString();

    const matchData = { ...rest, date: localISOString, modeSelected: this.form.value.sport.modes.indexOf(this.form.value.mode) };

    if (this.editingId) {
      this.matchService.updateMatch(this.editingId, matchData).subscribe(() => {
        this.snackBar.open('✅ Partido editado correctamente', 'Cerrar', {
            duration: 3000,
            panelClass: ['success-snackbar'], 
        });
        setTimeout(() => this.router.navigate(['/admin/matches']), 1000);
      });
    } else {
      this.matchService.createMatch(matchData).subscribe(() => {
        this.snackBar.open('✅ Partido creado correctamente', 'Cerrar', {
            duration: 3000,
            panelClass: ['success-snackbar'], 
        });
        setTimeout(() => this.router.navigate(['/admin/matches']), 1000);
      });
    }
  }
  cancel() {
    this.router.navigate(['/admin/matches']);
  }

  setupListeners() {
    this.form.get('club')?.valueChanges.subscribe((selectedClub: Club | null) => {
        
        this.sports = selectedClub?.sports ?? [];
        this.form.get('sport')?.reset();
        this.form.get('mode')?.reset();

        if (selectedClub && selectedClub.sports.length) {
        this.form.get('sport')?.enable();
        } else {
        this.form.get('sport')?.disable();
        this.form.get('mode')?.disable();
        }
    });

    this.form.get('sport')?.valueChanges.subscribe((selectedSport: Sport | null) => {
        this.modes = selectedSport?.modes ?? [];
        this.form.get('mode')?.reset();

        if (selectedSport && selectedSport.modes.length) {
        this.form.get('mode')?.enable();
        } else {
        this.form.get('mode')?.disable();
        }
    });
  }
}
