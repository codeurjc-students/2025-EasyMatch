import { TestBed } from '@angular/core/testing';
import { HttpClientModule, provideHttpClient, withInterceptors } from '@angular/common/http';
import { LoginService } from './login.service';
import { LoginRequest } from '../models/auth/login-request.model';
import { provideRouter } from '@angular/router';
import { User } from '../models/user.model';
import { credentialsInterceptor } from '../interceptors/auth.interceptor';

jasmine.DEFAULT_TIMEOUT_INTERVAL = 5000;

describe('LoginService', () => {
  let service: LoginService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        LoginService,
        provideRouter([]),
          provideHttpClient(
            withInterceptors([credentialsInterceptor])
          ),
      ]
    });

    service = TestBed.inject(LoginService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('login should authenticate and allow fetching current user', (done: DoneFn) => {
    const loginRequest: LoginRequest = {
      username: 'pedro@emeal.com',
      password: 'pedroga4'
    };

    service.login(loginRequest).subscribe({
      next: () => {

        service.currentUser$.subscribe({
          next: (user: User | null) => {

            expect(user).toBeTruthy();
            expect(user!.email).toBe(loginRequest.username);
            expect(user!.roles).toContain('USER');

            done();
          },
          error: err => {
            fail('getCurrentUser failed: ' + err);
            done();
          }
        });

      },
      error: err => {
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