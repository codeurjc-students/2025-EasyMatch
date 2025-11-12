import { TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { UserService } from './user.service';
import { User } from '../models/user.model';
import { LoginService } from './login.service';

jasmine.DEFAULT_TIMEOUT_INTERVAL = 15000;

describe('UserService', () => {
  let service: UserService;
  let loginService : LoginService;
  let loginRequest = {
    username: 'pedro@emeal.com',    
    password: 'pedroga4'
  }; 

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [UserService],
    });
    service = TestBed.inject(UserService);
    loginService =  TestBed.inject(LoginService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getCurrentUser should return the authenticated user', (done: DoneFn) => {
    loginService.login(loginRequest).subscribe({
      next: () => {
        service.getCurrentUser().subscribe({
          next: (user: User) => {
            expect(user).toBeTruthy();
            expect(user.id).toBe(1);
            expect(user.username).toBe("pedro123");
            expect(user.realname).toBe('Pedro Garcia');
            done();
          },
          error: (err) => {
            fail(`getCurrentUser failed : ${err.message}`);
            done();
          }
        });
      },
      error: (err) => {
        fail(`login failed : ${err.message}`);
        done();
      }
    });
    
  });

  it('deleteUser should delete the authenticated user', (done: DoneFn) =>{
    const testUserId = 1;
    loginService.login(loginRequest).subscribe({
      next: () => {
        service.deleteUser(testUserId).subscribe({
          next: (user: User) => {
            expect(user).toBeTruthy();
            expect(user.id).toBe(1);
            expect(user.username).toBe("pedro123");
            expect(user.realname).toBe('Pedro Garcia');
            done();
          },
          error: (err) => {
            fail(`deleteUser failed : ${err.message}`);
            done();
          }
        });
      },
      error: (err) => {
        fail(`login failed : ${err.message}`);
        done();
      }
    });
  });
});
