import { TestBed } from '@angular/core/testing';
import { HttpClientModule, provideHttpClient } from '@angular/common/http';
import { UserService } from './user.service';
import { User } from '../models/user.model';
import { LoginService } from './login.service';
import { provideRouter } from '@angular/router';

jasmine.DEFAULT_TIMEOUT_INTERVAL = 15000;
jasmine.getEnv().configure({ random: false });

describe('UserService', () => {
  let service: UserService;
  let loginService: LoginService;

  const loginRequest = {
    username: 'pedro@emeal.com',
    password: 'pedroga4'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        UserService,
        LoginService,
        provideRouter([]),
        provideHttpClient()
      ]
    });

    service = TestBed.inject(UserService);
    loginService = TestBed.inject(LoginService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  
  it('createUser should create a new user account', (done: DoneFn) => {
    const mockUser: Partial<User> = {
      realname: 'Juan',
      username: 'juanito24',
      password: 'juan3',
      email: 'juan@emeal.com',
      birthDate: new Date('1992-08-15T00:00:00'),
      gender: true,
      description: 'Soy Juan',
    };

    service.registerUser(mockUser).subscribe({
      next: (createdUser : User) => {
        expect(createdUser).toBeTruthy();
        expect(createdUser.level).toBe(0.0);
        expect(createdUser.stats.totalMatches).toBe(0);
        done();
      },
      error: err => {
        fail(`registerUser failed: ${err.message}`);
        done();
      }
    });
  });

  it('getCurrentUser should return the authenticated user', (done) => {
    loginService.login(loginRequest).subscribe({
      next: () => {
        service.getCurrentUser().subscribe({
          next: (user: User) => {
            expect(user).toBeTruthy();
            expect(user.id).toBe(1);
            expect(user.username).toBe('pedro123');
            expect(user.realname).toBe('Pedro Garcia');
            done();
          },
          error: err => {
            fail(`getCurrentUser failed : ${err.message}`);
            done();
          }
        });
      },
      error: err => {
        fail(`login failed : ${err.message}`);
        done();
      }
    });
  });

  it('deleteUser should delete the authenticated user', (done) => {
    const testUserId = 1;

    loginService.login(loginRequest).subscribe({
      next: () => {
        service.deleteUser(testUserId).subscribe({
          next: (user: User) => {
            expect(user.id).toBe(1);
            done();
          },
          error: err => {
            fail(`deleteUser failed : ${err.message}`);
            done();
          }
        });
      },
      error: err => {
        fail(`login failed : ${err.message}`);
        done();
      }
    });
  });

  

});
