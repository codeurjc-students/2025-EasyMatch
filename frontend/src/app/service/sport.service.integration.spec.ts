import { TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { SportService } from './sport.service';
import { LoginService } from './login.service';
import { Sport } from '../models/sport.model';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { ScoringType } from '../models/scoring-type';

jasmine.DEFAULT_TIMEOUT_INTERVAL = 15000;
jasmine.getEnv().configure({ random: false });

describe('SportService', () => {

  let service: SportService;
  let loginService: LoginService;

  const adminLoginRequest = {
    username: 'admin@emeal.com',
    password: 'admin'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        SportService,
        LoginService,
        provideRouter([]),
        provideHttpClient()
      ]
    });

    service = TestBed.inject(SportService);
    loginService = TestBed.inject(LoginService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });


  it('getSports should return list of sports', (done: DoneFn) => {
    service.getSports().subscribe({
      next: (sports: Sport[]) => {
        expect(sports).toBeTruthy();
        expect(Array.isArray(sports)).toBeTrue();
        expect(sports.length).toBeGreaterThan(0);
        done();
      },
      error: err => {
        fail(`getSports failed: ${err.message}`);
        done();
      }
    });
  });



  it('getSport should return the correct sport', (done: DoneFn) => {
    const id = 1;
    service.getSport(id).subscribe({
      next: (sport: Sport) => {
        expect(sport).toBeTruthy();
        expect(sport.id).toBe(id);
        expect(sport.name).toBeDefined();
        done();
      },
      error: err => {
        fail(`getSport failed: ${err.message}`);
        done();
      }
    });
  });

  it('createSport should create a new sport when admin is logged', (done: DoneFn) => {

    const mockSport: Sport = {
      name: 'Pickleball',
      modes: [{ name: 'Dobles', playersPerGame: 4 }],
      scoringType: ScoringType.SETS
    };

    loginService.login(adminLoginRequest).subscribe({
      next: () => {
        service.createSport(mockSport).subscribe({
          next: (created: Sport) => {
            expect(created).toBeTruthy();
            expect(created.id).toBeGreaterThan(0);
            expect(created.name).toBe('Pickleball');
            done();
          },
          error: err => {
            fail(`createSport failed: ${err.message}`);
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



  it('updateSport should edit an existing sport when admin is logged', (done: DoneFn) => {

    const mockSport: Sport = {
      name: 'Rugby',
      modes: [{ name: 'Union', playersPerGame: 15 }],
      scoringType: ScoringType.SCORE
    };

    loginService.login(adminLoginRequest).subscribe({
      next: () => {
        service.updateSport(1, mockSport).subscribe({
          next: (updated: Sport) => {
            expect(updated).toBeTruthy();
            expect(updated.name).toBe('Rugby');
            done();
          },
          error: err => {
            fail(`updateSport failed: ${err.message}`);
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



  it('deleteSport should remove a sport when admin is logged', (done: DoneFn) => {

    const sportToCreateAndDelete: Sport = {
      name: 'Futbol americano',
      modes: [{ name: 'Clasico', playersPerGame: 22 }],
      scoringType: ScoringType.SCORE
    };

    loginService.login(adminLoginRequest).subscribe({
      next: () => {

        service.createSport(sportToCreateAndDelete).subscribe({
          next: created => {
            expect(created.id).toBeGreaterThan(0);

            service.delete(created.id!).subscribe({
              next: deleted => {
                expect(deleted).toBeTruthy();
                expect(deleted.id).toBe(created.id);
                done();
              },
              error: err => {
                fail(`deleteSport failed: ${err.message}`);
                done();
              }
            });
          },
          error: err => {
            fail(`createSport failed: ${err.message}`);
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

});
