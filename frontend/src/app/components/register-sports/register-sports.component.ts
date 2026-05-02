// Angular core
import { Component, OnInit, inject } from '@angular/core';

// Forms
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';

// Angular
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

// Material
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';

// Services & models
import { UserService } from '../../service/user.service';
import { SportService } from '../../service/sport.service';
import { Sport } from '../../models/sport.model';
import { concatMap, from } from 'rxjs';

@Component({
  selector: 'app-register-sports',
  standalone: true,
  templateUrl: './register-sports.component.html',
  styleUrls: ['./register-sports.component.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatSelectModule
  ]
})
export class RegisterSportsComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly sportService = inject(SportService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  form!: FormGroup;

  sports: Sport[] = [];
  userId!: number;

  ngOnInit(): void {
    this.userId = history.state.userId;

    if (!this.userId) {
      this.router.navigate(['/register']);
      return;
    }

    this.form = this.fb.group({
      sports: [[]],
    });

    this.loadSports();
  }

  private loadSports(): void {
    this.sportService.getSports().subscribe({
      next: (sports) => {
        this.sports = sports;

        this.form.patchValue({
          sports: sports.map(s => ({
            sportId: s.id,
            frequency: 0
          }))
        });
      }
    });
  }

  save(): void {
    const selected = this.form.value.sports
      .filter((s: any) => s.frequency > 0);

    if (!selected.length) {
      this.router.navigate(['/login']);
      return;
    }

    from(selected).pipe(
      concatMap((s: any) => 
        this.userService.addSportToUser(this.userId, s.sportId, {
          level: this.mapFrequencyToLevel(s.frequency)
        })
      )
    ).subscribe({
      next: () => {
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('❌ Error al crear perfiles', 'Cerrar', { duration: 3000 });
      },
      complete: () => {
        this.snackBar.open('✅ Perfil deportivo creado', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/login']);
      }
    });
  }

  private mapFrequencyToLevel(freq: number): number {
    if (freq <= 1) return 2;
    if (freq <= 3) return 3;
    if (freq <= 5) return 4;
    return 5;
  }

  get sportsControls() {
    return this.form.get('sports')?.value;
  }

  updateFrequency(index: number, value: number): void {
    const sports = [...this.sportsControls];
    sports[index].frequency = value;
    this.form.patchValue({ sports });
  }
}