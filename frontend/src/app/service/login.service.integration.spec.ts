import { TestBed } from '@angular/core/testing';
import { HttpClientModule, provideHttpClient } from '@angular/common/http';
import { LoginService } from './login.service';
import { LoginRequest } from '../models/auth/login-request.model';
import { provideRouter } from '@angular/router';

jasmine.DEFAULT_TIMEOUT_INTERVAL = 5000;

describe('LoginService', () => {
  let service: LoginService;

  beforeEach(() => {
    TestBed.configureTestingModule({imports: [HttpClientModule],
      providers: [LoginService, provideRouter([]), provideHttpClient()],
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

    service.login(loginRequest).subscribe(
      response => {
        expect(response).toBeTruthy();
        expect(response.status).toEqual('SUCCESS');
        expect(response.message).toEqual('Auth successful. Tokens are created in cookie.');
        done();
      })
    });


  it('logout should return an AuthResponse from API', (done: DoneFn) => {
    service.logout().subscribe(
      response => {
        expect(response).toBeTruthy();
        expect(response.status).toEqual('SUCCESS');
        expect(response.message).toEqual('logout successfully');
        done();
      },
    )
  });
});