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

  const userLoginRequest = {
    username: 'pedro@emeal.com',
    password: 'pedroga4'
  };

  const adminLoginRequest = {
    username: 'admin@emeal.com',
    password: 'admin'
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
    loginService.login(userLoginRequest).subscribe({
      next: () => {
        service.getCurrentUser().subscribe({
          next: (user: User) => {
            expect(user).toBeTruthy();
            expect(user.id).toBe(2);
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
    const testUserId = 2;

    loginService.login(userLoginRequest).subscribe({
      next: () => {
        service.deleteUser(testUserId).subscribe({
          next: (user: User) => {
            expect(user.id).toBe(2);
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

  it('deleteUser should delete any user when admin is logged in', (done: DoneFn) => {

    const mockUser: Partial<User> = {
      realname: 'Carlos',
      username: 'carlitos55',
      password: 'test',
      email: 'carlitos55@emeal.com',
      birthDate: new Date(),
      gender: false,
      description: 'Test delete user'
    };


    service.registerUser(mockUser).subscribe({
      next: (createdUser: User) => {
        expect(createdUser).toBeTruthy();

        loginService.login(adminLoginRequest).subscribe({

          next: () => {
            service.deleteUser(createdUser.id!).subscribe({
              next: (response: any) => {
                expect(response).toBeTruthy();
                expect(response.id).toBe(createdUser.id);
                done();
              },
              error: err => {
                fail(`deleteUser failed: ${err.message}`);
                done();
              }
            });
          },

          error: err => {
            fail(`admin login failed: ${err.message}`);
            done();
          }
        });
      },

      error: err => {
        fail(`registerUser failed: ${err.message}`);
        done();
      }
    });
  });

  it('updateUser should edit any user when admin is logged in', (done: DoneFn) => {

    const mockUser: Partial<User> = {
      realname: 'Pepe',
      username: 'pepe22',
      password: 'test',
      email: 'pepe22@emeal.com',
      birthDate: new Date(),
      gender: true,
      description: 'Descripción original'
    };

    service.registerUser(mockUser).subscribe({
      next: (createdUser: User) => {

        const updatedUser: Partial<User> = {
          ...createdUser,
          realname: 'Pepe Modificado',
          description: 'Modificado por admin'
        };

        loginService.login(adminLoginRequest).subscribe({

          next: () => {
            service.updateUser(createdUser.id!, updatedUser).subscribe({
              next: updated => {
                expect(updated).toBeTruthy();
                expect(updated.realname).toBe('Pepe Modificado');
                expect(updated.description).toBe('Modificado por admin');
                done();
              },
              error: err => {
                fail(`replaceUser(admin) failed: ${err.message}`);
                done();
              }
            });
          },

          error: err => {
            fail(`admin login failed: ${err.message}`);
            done();
          }
        });
      },

      error: err => {
        fail(`registerUser failed: ${err.message}`);
        done();
      }
    });
  });


  

});
