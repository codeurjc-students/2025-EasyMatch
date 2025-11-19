import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatRadioModule } from '@angular/material/radio';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { UserService } from '../../service/user.service';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatRadioModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSlideToggleModule,
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  hidePassword = true;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}
  

  ngOnInit(): void {
    this.registerForm = this.fb.group({
        realname: ['', [Validators.required, Validators.minLength(3)]],
        username: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        birthDate: ['', Validators.required],
        gender: ['true', Validators.required],
        description: ['']
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.loading = true;

    const userData = {
      ...this.registerForm.value,
      gender: this.registerForm.value.gender === 'true',
    };

    this.userService.registerUser(userData).subscribe({
      next: () => {
        this.snackBar.open('✅ Cuenta creada correctamente', 'Cerrar', {
          duration: 3000,
          panelClass: ['success-snackbar'], 
        });
        setTimeout(() => this.router.navigate(['/login']), 1000);
      },
      error: (err) => {
        console.error('Error en el registro:', err);
        this.snackBar.open('❌ Error al crear la nueva cuenta', 'Cerrar', {
            duration: 4000,
            panelClass: ['error-snackbar'],
        });
        this.loading = false;
      },
      complete: () => (this.loading = false),
    });
  }
  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
