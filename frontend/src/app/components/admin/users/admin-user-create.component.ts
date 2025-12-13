import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../service/user.service';
import { User } from '../../../models/user.model';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSnackBar } from '@angular/material/snack-bar';

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
export class AdminUserCreateComponent implements OnInit {

  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);
  form!: FormGroup;
  editingId: number | null = null;
  photoFile: File | null = null;
  photoPreview: string | null = null;


  
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
    this.route.queryParams.subscribe(params => {
      if (params['id']) {
        this.editingId = +params['id'];
        this.loadUser(+params['id']);
      }
    });
  };

  
  loadUser(id: number) {
  this.userService.getUserById(id).subscribe({
    next: (u: User) => {
      this.form.patchValue({
        realname: u.realname,
        username: u.username,
        email: u.email,
        birthDate: u.birthDate,
        gender: u.gender,
        level: u.level,
        description: u.description,
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

    const payload = this.form.value;

    if (this.editingId) {
      this.userService.updateUser(this.editingId, payload).subscribe({
        next: (user : User) => {
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
      });

    } else {
      this.userService.registerUser(payload).subscribe({
        next: (created: any) => {
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
  
  onPhotoSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    this.photoFile = file;

    const reader = new FileReader();
    reader.onload = () => this.photoPreview = reader.result as string;
    reader.readAsDataURL(file);
  }

  cancel() {
    this.router.navigate(['/admin/users']);
  }
}
