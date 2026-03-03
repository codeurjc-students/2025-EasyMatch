import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

import { UserService } from '../../service/user.service';
import { User } from '../../models/user.model';
import { HeaderComponent } from '../header/header.component';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { environment } from '../../../environments/environment';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NgxChartsModule, ScaleType } from '@swimlane/ngx-charts';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    NgxChartsModule,
    HeaderComponent
  ],
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);

  user = signal<User | null>(null);
  loading = signal(true);
  editing = signal(false);
  saving = signal(false);
  photoFile: File | null = null;
  photoPreview: string | null = null;
  levelChartData = signal<any[]>([]);
  colorScheme = {
    name: 'blueScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#005CBB']
  };

  private apiUrl = environment.apiUrl;
  form!: FormGroup;


  ngOnInit(): void {
    this.form = this.fb.group({
      realname: ['', Validators.required],
      username: ['', Validators.required],
      password: [''],
      email: ['', [Validators.required, Validators.email]],
      birthDate: ['', Validators.required],
      gender: [true, Validators.required],
      description: [''],
      level: ['', [Validators.required, Validators.min(0), Validators.max(7)]],
    });

    this.userService.getCurrentUser().subscribe({
      next: (data) => {
        this.user.set(data);
        this.patchForm(data);
        this.loadUserImage(data.id);
        this.buildLevelChart(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando usuario:', err);
        this.loading.set(false);
      }
    });
  }

  loadUserImage(id: number): void {
    this.userService.getUserImage(id).subscribe({
      next: (blob: Blob) => {
        const reader = new FileReader();
        reader.onload = () => (this.photoPreview = reader.result as string);
        reader.readAsDataURL(blob);
      },
      error: () => {
        this.photoPreview = null;
      }
    });
  }

  onPhotoSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    this.photoFile = file;

    const reader = new FileReader();
    reader.onload = () => (this.photoPreview = reader.result as string);
    reader.readAsDataURL(file);
  }

  patchForm(user: User): void {
    this.form.patchValue({
      realname: user.realname,
      username: user.username,
      email: user.email,
      birthDate: user.birthDate,
      gender: user.gender,
      description: user.description ?? '',
      level: user.level
    });
  }

  getUserImage(id: number): string {
    return `${this.apiUrl}/users/${id}/image`;
  }

  startEdit(): void {
    if (!this.user()) return;
    this.patchForm(this.user()!);
    this.editing.set(true);
  }

  cancelEdit(): void {
    this.editing.set(false);
  }

  save(): void {
    if (!this.user() || this.form.invalid) return;

    this.saving.set(true);

    const raw = this.form.value;

    const payload = {
      ...raw,
      realname: raw.realname,
      username: raw.username,
      email: raw.email,
      birthDate: this.toLocalMidnightISOString(raw.birthDate),
      gender: raw.gender,
      description: raw.description,
      level: raw.level
    };

    this.userService.updateUser(this.user()!.id, payload).subscribe({
      next: (updated) => {
        if (this.photoFile) {
          this.userService.replaceUserImage(updated.id, this.photoFile).subscribe({
            next: () => this.afterSuccessfulSave(updated, true),
            error: (err) => {
              console.error('Error al subir foto:', err);
              this.afterSuccessfulSave(updated, false);
            }
          });
        } else {
          this.afterSuccessfulSave(updated, true);
        }
      },
      error: (err) => {
        console.error('Error actualizando perfil:', err);
        this.saving.set(false);
        this.snackBar.open('❌ Error al actualizar el perfil', 'Cerrar', {
          duration: 4000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onDeleteAccount(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Eliminar cuenta',
        message:
          '¿Estás seguro de que quieres eliminar tu cuenta? Esta acción es permanente y no se puede deshacer.',
        confirmText: 'Eliminar cuenta',
        cancelText: 'Cancelar'
      }
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.userService.deleteUser(id).subscribe({
          next: () => {
            this.snackBar.open(
              '✅ Tu cuenta ha sido eliminada correctamente',
              'Cerrar',
              { duration: 3000, panelClass: ['success-snackbar'] }
            );
            this.router.navigateByUrl('/');
          },
          error: (err) => {
            console.error('Error al eliminar cuenta:', err);
            this.snackBar.open('❌ Error al eliminar la cuenta. Inténtalo de nuevo', 'Cerrar', {
              duration: 4000,
              panelClass: ['error-snackbar']
            });
          }
        });
      }
    });
  }

  goToMyMatches(): void {
    this.router.navigate(['/my-matches']);
  }
  
  get maxLevel(): number{
    const u = this.user();
    if(!u) return 0;

    let maxLevel = u.level;
    for(const entry of u.levelHistory ?? []){
      if (entry.levelBefore > maxLevel ){
        maxLevel = entry.levelBefore;
      }
    }
    return maxLevel;
  }

  private afterSuccessfulSave(user: User, photoOk: boolean): void {
    this.user.set(user);
    this.editing.set(false);
    this.saving.set(false);
    this.photoFile = null;

    if (photoOk) {
      this.snackBar.open('✅ Perfil actualizado correctamente', 'Cerrar', {
        duration: 3000,
        panelClass: ['success-snackbar']
      });
    } else {
      this.snackBar.open(
        '⚠️ Perfil guardado, pero error subiendo la foto',
        'Cerrar',
        { duration: 4000, panelClass: ['error-snackbar'] }
      );
    }
  }
  private toLocalMidnightISOString(date: Date): string {
    const localDate = new Date(date);
    localDate.setHours(0, 0, 0, 0);

    return new Date(
      localDate.getTime() - localDate.getTimezoneOffset() * 60000
    ).toISOString();
  }

  
  private buildLevelChart(user: User): void {
    if (!user.levelHistory?.length) {
      this.levelChartData.set([]);
      return;
    }

    const series = user.levelHistory.map(h => ({
      name: new Date(h.date).toLocaleDateString(),
      value: h.levelBefore
    }));

    series.push({
      name: 'Actual',
      value: user.level
    });

    this.levelChartData.set([
      {
        name: 'Nivel',
        series
      }
    ]);
  }

}

