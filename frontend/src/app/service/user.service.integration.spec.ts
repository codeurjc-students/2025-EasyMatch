import { TestBed } from '@angular/core/testing';
import { HttpClientModule, provideHttpClient, withInterceptors } from '@angular/common/http';
import { UserService } from './user.service';
import { User } from '../models/user.model';
import { LoginService } from './login.service';
import { provideRouter } from '@angular/router';
import { credentialsInterceptor } from '../interceptors/auth.interceptor';

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
        provideHttpClient(withInterceptors([credentialsInterceptor]))
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
      email: 'juan2@emeal.com',
      birthDate: new Date('1992-08-15T00:00:00'),
      gender: true,
      description: 'Soy Juan',
    };

    service.registerUser(mockUser).subscribe({
      next: (createdUser : User) => {
        expect(createdUser).toBeTruthy();
        expect(createdUser.roles).toContain("USER");
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
          next: (user: User | null) => {
            expect(user).toBeTruthy();
            expect(user!.id).toBe(2);
            expect(user!.username).toBe('pedro123');
            expect(user!.realname).toBe('Pedro Garcia');
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

  it('deleteUser should delete the authenticated user', (done: DoneFn) => {
    const mockUser: Partial<User> = {
      realname: 'Delete Test',
      username: 'deleteuser123',
      password: 'test123',
      email: 'deleteuser123@emeal.com',
      birthDate: new Date(),
      gender: true,
      description: 'User for self delete test'
    };

    service.registerUser(mockUser).subscribe({
      next: (createdUser: User) => {

        const loginRequest = {
          username: mockUser.email!,
          password: mockUser.password!
        };

        loginService.login(loginRequest).subscribe({
          next: () => {
            service.deleteUser(createdUser.id!).subscribe({
              next: (deletedUser: User) => {
                expect(deletedUser).toBeTruthy();
                expect(deletedUser.id).toBe(createdUser.id);
                done();
              },
              error: err => {
                fail(`deleteUser failed: ${err.message}`);
                done();
              }
            });
          },
          error: err => {
            fail(`login failed: ${err.message}`);
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

  it('getUserMatches should return user matches', (done: DoneFn) => {
    const userId = 2;

    loginService.login(userLoginRequest).subscribe({
      next: () => {
        service.getUserMatches(userId).subscribe({
          next: (matches) => {
            expect(matches).toBeTruthy();
            expect(Array.isArray(matches)).toBeTrue();
            done();
          },
          error: err => {
            fail(`getUserMatches failed: ${err.message}`);
            done();
          }
        });
      },
      error: err => {
        fail(`login failed: ${err.message}`);
        done();
      }
    });
  });

  it('getAllUsers should return paginated users', (done: DoneFn) => {
    loginService.login(adminLoginRequest).subscribe({
      next: () => {
        service.getAllUsers(0, 10, 'id,asc').subscribe({
          next: (response) => {
            expect(response).toBeTruthy();
            expect(response.content).toBeTruthy();
            expect(Array.isArray(response.content)).toBeTrue();
            expect(response.totalElements).toBeGreaterThanOrEqual(0);
            done();
          },
          error: err => {
            fail(`getAllUsers failed: ${err.message}`);
            done();
          }
        });
      },
      error: err => {
        fail(`admin login failed: ${err.message}`);
        done();
      }
    });
  });

  it('getUserById should return a user by id', (done: DoneFn) => {
    const userId = 2;

    loginService.login(adminLoginRequest).subscribe({
      next: () => {
        service.getUserById(userId).subscribe({
          next: (user) => {
            expect(user).toBeTruthy();
            expect(user.id).toBe(userId);
            done();
          },
          error: err => {
            fail(`getUserById failed: ${err.message}`);
            done();
          }
        });
      },
      error: err => {
        fail(`admin login failed: ${err.message}`);
        done();
      }
    });
  });

  it('getUserSports should return user sports', (done: DoneFn) => {
    const userId = 2;

    loginService.login(userLoginRequest).subscribe({
      next: () => {
        service.getUserSports(userId).subscribe({
          next: (sports) => {
            expect(sports).toBeTruthy();
            expect(Array.isArray(sports)).toBeTrue();
            done();
          },
          error: err => {
            fail(`getUserSports failed: ${err.message}`);
            done();
          }
        });
      },
      error: err => {
        fail(`login failed: ${err.message}`);
        done();
      }
    });
  });

  it('getUserSportProfile should return sport profile', (done: DoneFn) => {
    const userId = 2;
    const sportId = 1;

    loginService.login(userLoginRequest).subscribe({
      next: () => {
        service.getUserSportProfile(userId, sportId).subscribe({
          next: (profile) => {
            expect(profile).toBeTruthy();
            done();
          },
          error: err => {
            fail(`getUserSportProfile failed: ${err.message}`);
            done();
          }
        });
      },
      error: err => {
        fail(`login failed: ${err.message}`);
        done();
      }
    });
  });

  it('getUserSportHistory should return sport level history', (done: DoneFn) => {
    const userId = 2;
    const sportId = 1;

    loginService.login(userLoginRequest).subscribe({
      next: () => {
        service.getUserSportHistory(userId, sportId).subscribe({
          next: (history) => {
            expect(history).toBeTruthy();
            expect(Array.isArray(history)).toBeTrue();
            done();
          },
          error: err => {
            fail(`getUserSportHistory failed: ${err.message}`);
            done();
          }
        });
      },
      error: err => {
        fail(`login failed: ${err.message}`);
        done();
      }
    });
  });

});
