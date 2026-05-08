import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../service/user.service';
import { User } from '../../../models/user.model';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Sport } from '../../../models/sport.model';
import { SportService } from '../../../service/sport.service';
import { Subject } from 'rxjs/internal/Subject';
import { takeUntil } from 'rxjs/internal/operators/takeUntil';

@Component({
  selector: 'app-admin-user-create',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatDatepickerModule
],
  templateUrl: './admin-user-create.component.html',
  styleUrls: ['.././admin-entity-create.component.scss']
})
export class AdminUserCreateComponent implements OnInit,OnDestroy {

  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private sportService = inject(SportService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);
  private location = inject(Location);
  private originalProfiles = new Map<number, number>();
  private destroy$ = new Subject<void>();

  form!: FormGroup;
  editingId: number | null = null;
  isEditMode = false;
  photoFile: File | null = null;
  photoPreview: string | null = null;
  sports: Sport[] = [];
  

  get sportLevels(): FormArray {
    return this.form.get('sportLevels') as FormArray;
  }
  
  ngOnInit(): void {
    this.form = this.fb.group({
      realname: ['', Validators.required],
      username: ['', Validators.required],
      password: [''],
      email: ['', [Validators.required, Validators.email]],
      birthDate: ['', Validators.required],
      gender: [true, Validators.required],
      description: [''],
      sportLevels: this.fb.array([])
    });

    this.sportService.getSports().subscribe({
      next: (sports) => this.sports = sports
    });

    this.route.queryParams.subscribe(params => {
      if (params['id']) {
        this.editingId = +params['id'];
        this.isEditMode = true;
        this.loadUser(this.editingId);
      } else {
        this.addSportLevel();
      }
    });
  };

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.snackBar.dismiss();
  }

  private createSportLevelGroup(data?: any): FormGroup {
    return this.fb.group({
      profileId: [data?.profileId ?? null],
      sportId: [data?.sportId ?? '', Validators.required],
      level: [
        data?.level ?? '',
        [Validators.required, Validators.min(0), Validators.max(7)]
      ]
    });
  }

  addSportLevel(): void {
    this.sportLevels.push(this.createSportLevelGroup());
  }

  
  removeSportLevel(index: number): void {
    if (this.isEditMode) return;
    this.sportLevels.removeAt(index);
  }

  loadUser(id: number) {
    this.userService.getUserById(id).subscribe({
      next: (u: User) => {
        this.form.patchValue({
          realname: u.realname,
          username: u.username,
          email: u.email,
          birthDate: u.birthDate,
          gender: u.gender,
          description: u.description,
        });
      
      this.userService.getUserSports(id).subscribe({
        next: (sports) => {
          this.sportLevels.clear();

          sports.forEach(s => {
            this.userService.getUserSportProfile(id, s.id!).subscribe({
              next: (profile) => {
                this.originalProfiles.set(profile.id, profile.level);
                
                const group = this.createSportLevelGroup({
                  profileId: profile.id,
                  sportId: s.id,
                  level: this.roundToTwoDecimals(profile.level),
                });
                this.sportLevels.push(group);
              }
            });
          });

          if (!sports.length) this.addSportLevel();
        }
      });
      
      this.userService.getUserImage(id).subscribe({
        next: (blob: Blob) => {
          const reader = new FileReader();
          reader.onload = () => this.photoPreview = reader.result as string;
          reader.readAsDataURL(blob);
        },
        error: () => {
          this.photoPreview = null; 
        }
      });
    }
  });
}


   async save() {
    if (this.form.invalid) return;

    const raw = this.form.value;

    const payload = {
      ...raw,
      birthDate: this.toLocalMidnightISOString(raw.birthDate)
    };

    if (this.editingId) {
      this.userService.updateUser(this.editingId, payload)
      .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (user : User) => {
            this.syncSportProfiles(user.id); 
            if (this.photoFile) {
              this.userService.replaceUserImage(user.id, this.photoFile).subscribe({
                next: () => {
                  this.snackBar.open('✅ Usuario editado correctamente', 'Cerrar', { duration: 3000, panelClass: ['success-snackbar'] });
                  this.reloadAfterSave();
                },
                error: (err) => {
                  console.error('Error al subir foto:', err);
                  this.snackBar.open('❌ Usuario guardado pero error subiendo la foto', 'Cerrar', { duration: 4000, panelClass: ['error-snackbar'] });
                  this.reloadAfterSave();
                }
              });
            } else {
              this.snackBar.open('✅ Usuario editado correctamente', 'Cerrar', { duration: 3000, panelClass: ['success-snackbar'] });
              this.reloadAfterSave();
            }
          },
          error: (err) => {
            console.error('Error al actualizar usuario:', err);
            this.snackBar.open('❌ Error al editar usuario', 'Cerrar', { duration: 4000, panelClass: ['error-snackbar'] });
          }
        }
      );

    } else {
      this.userService.registerUser(payload).subscribe({
        next: (created: User) => {
          this.syncSportProfiles(created.id); 
          const newId = created?.id;
          if (this.photoFile && newId) {
            this.userService.replaceUserImage(newId, this.photoFile).subscribe({
              next: () => {
                this.snackBar.open('✅ Usuario creado correctamente', 'Cerrar', { duration: 3000, panelClass: ['success-snackbar'] });
                this.router.navigate(['/admin/users']);
              },
              error: (err) => {
                console.error('Error al subir foto tras crear usuario:', err);
                this.snackBar.open('❌ Usuario creado, pero error subiendo foto', 'Cerrar', { duration: 4000, panelClass: ['error-snackbar'] });
                this.router.navigate(['/admin/users']);
              }
            });
          } else {
            this.snackBar.open('✅ Usuario creado correctamente', 'Cerrar', { duration: 3000, panelClass: ['success-snackbar'] });
            this.router.navigate(['/admin/users']);
          }
        },
        error: (err) => {
          console.error('Error al crear usuario:', err);
          this.snackBar.open('❌ Error al crear usuario', 'Cerrar', { duration: 4000, panelClass: ['error-snackbar'] });
        }
      });
    }
  }

  private reloadAfterSave() {
    if (this.editingId) {
      this.userService.getUserImage(this.editingId).subscribe({
        next: (blob: Blob) => {
          const reader = new FileReader();
          reader.onload = () => this.photoPreview = reader.result as string;
          reader.readAsDataURL(blob);
          setTimeout(() => this.router.navigate(['/admin/users']), 800);
        },
        error: () => this.router.navigate(['/admin/users'])
      });
    } else {
      this.router.navigate(['/admin/users']);
    }
  }

  private syncSportProfiles(userId: number): void {
    const profiles = this.form.getRawValue().sportLevels;

    profiles.forEach((p: any) => {

      if (this.isEditMode && p.profileId && p.level === this.getOriginalLevel(p.profileId)) {
        return;
      }

      const payload = {
        level: this.roundToTwoDecimals(p.level)
      };

      const request$ = p.profileId
        ? this.userService.updateSportProfile(userId, p.sportId, payload)
        : this.userService.addSportToUser(userId, p.sportId, payload);

      request$.subscribe({
        next: (res) => console.log('PROFILE OK', res),
        error: (err) => console.error('PROFILE ERROR', err)
      });
    });
  }
  
  onPhotoSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    this.photoFile = file;

    const reader = new FileReader();
    reader.onload = () => this.photoPreview = reader.result as string;
    reader.readAsDataURL(file);
  }

  private toLocalMidnightISOString(date: Date): string {
    const localDate = new Date(date);
    localDate.setHours(0, 0, 0, 0);

    return new Date(
      localDate.getTime() - localDate.getTimezoneOffset() * 60000
    ).toISOString();
  }
  
  cancel() {
    this.location.back();
  }

  private roundToTwoDecimals(value: number): number {
    return Math.round(value * 100) / 100;
  }

  private getOriginalLevel(profileId: number): number | null {
    return this.originalProfiles.get(profileId) ?? null;
  }

  getSelectedSportIds(): number[] {
    return this.sportLevels.controls
      .map(c => c.get('sportId')?.value)
      .filter(v => v !== null && v !== undefined && v !== '');
  }

  isBackendProfile(i: number): boolean {
    return !!this.sportLevels.at(i).get('profileId')?.value;
  }

  isSportDisabled(sportId: number, i: number): boolean {

    const control = this.sportLevels.at(i);
    const isBackend = !!control.get('profileId')?.value;

    const selected = this.getSelectedSportIds();
    const currentValue = control.get('sportId')?.value;

    if (isBackend) {
      return true;
    }

    return selected.includes(sportId) && currentValue !== sportId;
  }
}
