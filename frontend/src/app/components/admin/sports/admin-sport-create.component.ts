import { Component, inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SportService } from '../../../service/sport.service';
import { Sport } from '../../../models/sport.model';
import { ScoringType } from '../../../models/scoring-type';

@Component({
  selector: 'app-admin-sport-create',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule
  ],
  templateUrl: './admin-sport-create.component.html',
  styleUrls: ['.././admin-entity-create.component.scss']
})
export class AdminSportCreateComponent implements OnInit {

  private fb = inject(FormBuilder);
  private sportService = inject(SportService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);

  form!: FormGroup;
  editingId: number | null = null;

  scoringTypes = [
    { value: ScoringType.SCORE, label: 'Por puntos' },
    { value: ScoringType.SETS, label: 'Por sets' }
  ];

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      scoringType: ['', Validators.required],
      modes: this.fb.array([])  
    });

    this.route.queryParams.subscribe(params => {
      if (params['id']) {
        this.editingId = +params['id'];
        this.loadSport(this.editingId);
      } else {
        this.addMode(); 
      }
    });
  }


  get modesArray(): FormArray {
    return this.form.get('modes') as FormArray;
  }

  addMode() {
    this.modesArray.push(
      this.fb.group({
        name: ['', Validators.required],
        playersPerGame: [1, [Validators.required, Validators.min(1)]]
      })
    );
  }

  removeMode(i: number) {
    this.modesArray.removeAt(i);
  }

  loadSport(id: number) {
    this.sportService.getSport(id).subscribe({
      next: (s: Sport) => {
        this.form.patchValue({
          name: s.name,
          scoringType: s.scoringType
        });

        this.modesArray.clear();
        s.modes.forEach(m => {
          this.modesArray.push(this.fb.group({
            name: [m.name, Validators.required],
            playersPerGame: [m.playersPerGame, [Validators.required, Validators.min(1)]]
          }));
        });
      }
    });
  }

  save() {
    if (this.form.invalid) return;

    const sport: Sport = this.form.value;

    if (this.editingId) {
      this.sportService.updateSport(this.editingId, sport).subscribe({
        next: () => {
          this.snackBar.open('✅ Deporte actualizado correctamente', 'Cerrar', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.router.navigate(['/admin/sports']);
        },
        error: () => {
          this.snackBar.open('❌ Error al actualizar deporte', 'Cerrar', {
            duration: 4000,
            panelClass: ['error-snackbar']
          });
        }
      });

    } else {
      this.sportService.createSport(sport).subscribe({
        next: () => {
          this.snackBar.open('✅ Deporte creado correctamente', 'Cerrar', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.router.navigate(['/admin/sports']);
        },
        error: () => {
          this.snackBar.open('❌ Error al crear deporte', 'Cerrar', {
            duration: 4000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }

  cancel() {
    this.router.navigate(['/admin/sports']);
  }
}
