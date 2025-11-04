import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { LoginComponent } from './login.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let compiled: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        LoginComponent,
        HttpClientTestingModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    compiled = fixture.nativeElement;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should render the welcome message', () => {
    const message = compiled.querySelector('#welcome-message');
    expect(message?.textContent).toContain('Bienvenido de vuelta');
  });

  it('should have email and password fields', () => {
    const emailInput = compiled.querySelector('input[formControlName="email"]');
    const passwordInput = compiled.querySelector('input[formControlName="password"]');
    expect(emailInput).toBeTruthy();
    expect(passwordInput).toBeTruthy();
  });

  it('should mark email as required when empty', () => {
    const emailControl = component.loginForm.get('email');
    emailControl?.setValue('');
    emailControl?.markAsTouched();
    fixture.detectChanges();

    const error = compiled.querySelector('mat-error');
    expect(error?.textContent).toContain('Campo obligatorio');
  });

  it('should disable the submit button when loading', () => {
    component.loading.set(true);
    fixture.detectChanges();

    const button = compiled.querySelector('.btn-login') as HTMLButtonElement;
    expect(button.disabled).toBeTrue();
    expect(button.textContent).toContain('Iniciando...');
  });

  it('should toggle password visibility when togglePasswordVisibility is called', () => {
    const initial = component.hide();
    component.togglePasswordVisibility();
    const after = component.hide();
    expect(after).toBe(!initial);
  });

  it('should display an error message if errorMessage signal has value', () => {
    component.errorMessage.set('Credenciales incorrectas');
    fixture.detectChanges();

    const error = compiled.querySelector('.error');
    expect(error?.textContent).toContain('Credenciales incorrectas');
  });

  it('should call onSubmit when form is submitted and valid', () => {
    const spy = spyOn(component, 'onSubmit');
    const form = compiled.querySelector('form');
    component.loginForm.setValue({ email: 'test@example.com', password: '123456' });

    form?.dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(spy).toHaveBeenCalled();
  });
});