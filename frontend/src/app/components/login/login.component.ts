import { Component, inject, signal, ViewEncapsulation} from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { NgIf } from '@angular/common';
import { LoginService } from '../../service/login.service';
import { LoginRequest } from '../../models/auth/login-request.model';
import { Router, RouterLink } from '@angular/router';
import { ErrorService } from '../../service/error.service';
import { AuthResponse } from '../../models/auth/auth-response.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  encapsulation: ViewEncapsulation.None,
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    NgOptimizedImage,
    NgIf,
    CommonModule,
    RouterLink
],
  
})
export class LoginComponent {
  public loginForm: FormGroup;

  constructor(private loginService: LoginService, private fb: FormBuilder, private router: Router, private errorService: ErrorService) {
		this.loginForm = this.fb.nonNullable.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
	}

  hide = signal(true);
  loading = signal(false);
  errorMessage = signal<string | null>(null);


  togglePasswordVisibility() {
    this.hide.set(!this.hide());
  }

  private getLoginRequest() {
		const data = this.loginForm.value;
    
		const loginRequest: LoginRequest = {
			username: data.email!,
			password: data.password!
		};
		return loginRequest;
	}

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const loginRequest = this.getLoginRequest();

    this.loginService.login(loginRequest).subscribe({
      next: (response : AuthResponse) => {
        this.loading.set(false);
        if (response.authorities.match('ROLE_ADMIN')){
          this.router.navigate(['/admin']);
        }else{
          this.router.navigate(['/matches']);
        }
        
      },
      error: (err) => {
        this.loading.set(false);
        if (err.status === 401) {
          this.errorMessage.set('Credenciales inválidas');
        } else {
          this.errorMessage.set('Error inesperado. Inténtalo más tarde.');
        }
        this.errorService.setError(err.status ?? 500, err.message ?? 'Error desconocido');
        
        this.router.navigate(['/']);
      },
    });
  }
}
