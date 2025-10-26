import { TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { LoginService } from './login.service';
import { LoginRequest } from '../models/auth/login-request.model';
import { AuthResponse } from '../models/auth/auth-response.model';

jasmine.DEFAULT_TIMEOUT_INTERVAL = 15000;

describe('LoginService Integration Test', () => {
  let service: LoginService;

  beforeEach(() => {
    TestBed.configureTestingModule({imports: [HttpClientModule],
      providers: [LoginService],
    });
    service = TestBed.inject(LoginService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('login should return an AuthResponse from API', (done: DoneFn) => {
    const loginRequest: LoginRequest = {
      username: 'pedro@emeal.com',    
      password: 'pedroga4'
    };

    service.login(loginRequest).subscribe({
      next: (response: AuthResponse) => {
        expect(response).toBeTruthy();
        expect(response.status).toEqual('SUCCESS');
        expect(response.message).toEqual('Auth successful. Tokens are created in cookie.');
        done();
      },
      error: (err) => {
        fail(`Login failed: ${JSON.stringify(err)}`);
        done();
      }
    });
  });

  it('logout should return an AuthResponse from API', (done: DoneFn) => {
    service.logout().subscribe({
      next: (response: AuthResponse) => {
        expect(response).toBeTruthy();
        expect(response.status).toEqual('SUCCESS');
        expect(response.message).toEqual('logout successfully');
        done();
      },
      error: (err) => {
        fail(`Logout failed: ${JSON.stringify(err)}`);
        done();
      }
    });
  });
});