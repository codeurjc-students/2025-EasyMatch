import { TestBed } from '@angular/core/testing';
import { HttpClientModule, provideHttpClient } from '@angular/common/http';
import { LoginService } from './login.service';
import { LoginRequest } from '../models/auth/login-request.model';
import { provideRouter } from '@angular/router';

jasmine.DEFAULT_TIMEOUT_INTERVAL = 5000;

describe('LoginService', () => {
 let service: LoginService;

  beforeEach(() => {

    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        LoginService,
        provideRouter([])
      ]
    });

    service = TestBed.inject(LoginService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('login should return a token from real API', (done: DoneFn) => {

    const loginRequest: LoginRequest = {
      username: 'pedro@emeal.com',
      password: 'pedroga4'
    };

    service.login(loginRequest).subscribe({
      next: (response) => {

        expect(response).toBeTruthy();
        expect(response.status).toBe('SUCCESS');
        expect(response.authorities).toContain('ROLE_USER');

        done();
      },
      error: (err) => {
        fail('Login request failed: ' + err);
        done();
      }
    });

  });

  it('logout should return AuthResponse from real API', (done: DoneFn) => {

    service.logout().subscribe({
      next: (response) => {

        expect(response).toBeTruthy();
        expect(response.status).toEqual('SUCCESS');

        done();
      },
      error: (err) => {
        fail('Logout request failed: ' + err);
        done();
      }
    });

  });

});