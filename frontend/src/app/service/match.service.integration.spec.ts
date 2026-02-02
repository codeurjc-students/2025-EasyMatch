import { TestBed } from "@angular/core/testing";
import { MatchService } from "./match.service";
import { HttpClientModule } from "@angular/common/http";
import { Sport } from "../models/sport.model";
import { Club } from "../models/club.model";
import { Match } from "../models/match.model";
import { LoginService } from "./login.service";
import { ScoringType } from "../models/scoring-type";
import { MatchResult } from "../models/match-result.model";

jasmine.DEFAULT_TIMEOUT_INTERVAL = 15000;
jasmine.getEnv().configure({ random: false });

describe('MatchService', () => {
    let service : MatchService;
    let loginService : LoginService;
    let loginRequest = {
        username: 'pedro@emeal.com',    
        password: 'pedroga4'
    };
    let adminLoginRequest = {
      username: 'admin@emeal.com',
      password: 'admin'
    };

    const mockSport: Sport = {
      id: 1,
      name: 'Fútbol',
      modes: [{ name: '7v7', playersPerGame: 14 }],
      scoringType: ScoringType.SCORE
    };

    const mockClub: Club = {
      id: 1,
      name: 'Club Test',
      city: 'Madrid',
      address: 'Calle Falsa 123',
      sports: [mockSport],
      schedule: { openingTime: '09:00', closingTime: '22:00' },
      priceRange: { minPrice: 10, maxPrice: 20, unit: '€/hora' },
    };

    const mockMatch: Partial<Match> = {
      date: new Date(),
      type: true,
      isPrivate: false,
      sport: mockSport,
      price: 15,
      club: mockClub,
    };

    beforeEach(() =>{
        TestBed.configureTestingModule({imports:[HttpClientModule], providers: [MatchService]});
        service = TestBed.inject(MatchService);
        loginService = TestBed.inject(LoginService);
    });
    
    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('getMatches should return API\'s value as an Observable', (done: DoneFn) => {
        service.getMatches(0, 10,'').subscribe(response => {
            expect(response).toBeTruthy();
            expect(Array.isArray(response.content)).toBeTrue();
            expect(response.content.length).toBeGreaterThan(0);
            done();
        });
    });

   it('createMatch should send a valid payload and return the created match', (done: DoneFn) => {

    loginService.login(loginRequest).subscribe(
      response => {
        expect(response).toBeTruthy();
        expect(response.status).toEqual('SUCCESS');
        expect(response.message).toEqual('Auth successful. Tokens are created in cookie.');

        service.createMatch(mockMatch).subscribe(
          createdMatch => {
            expect(createdMatch).toBeTruthy();
            expect(createdMatch.organizer.realname).toBe('Pedro Garcia');
            expect(createdMatch.state).toBeTrue();
            expect(createdMatch.date instanceof Date).toBeTrue();
            done();
        });
      })
   });
   it('joinMatch should add the logged user to the match\'s players', (done: DoneFn) => {
    const matchId = 5; 
    loginService.login(loginRequest).subscribe(
      response => {
        expect(response).toBeTruthy();
        expect(response.status).toEqual('SUCCESS');
        expect(response.message).toEqual('Auth successful. Tokens are created in cookie.');
        service.joinMatch(matchId, 'A').subscribe(
          response => {
            expect(response).toBeTruthy();
            expect(response.status).toContain('SUCCESS');
            expect(response.message).toContain('Player added to team A');
            done();
          });
      })
   });

   it('leaveMatch should remove the logged user from the match\'s players', (done: DoneFn) => {
    const matchId = 5;
    loginService.login(loginRequest).subscribe(
      response => {
        expect(response).toBeTruthy();
        expect(response.status).toEqual('SUCCESS');
        expect(response.message).toEqual('Auth successful. Tokens are created in cookie.');
        service.leaveMatch(matchId).subscribe(
          response => {
            expect(response).toBeTruthy();
            expect(response.status).toContain('SUCCESS');
            expect(response.message).toContain('Player removed from match');
            done();
          });
      })
   });

   it('updateMatch should update match fields when admin is logged in', (done: DoneFn) => {
    loginService.login(adminLoginRequest).subscribe({
      next: () => {

        service.createMatch(mockMatch).subscribe({
          next: (created: Match) => {
            expect(created).toBeTruthy();

            const updatedMatch: Partial<Match> = {
              ...created,
              price: 99.99,
              isPrivate: !created.isPrivate
            };

            service.updateMatch(created.id!, updatedMatch).subscribe({
              next: updated => {
                expect(updated).toBeTruthy();
                expect(updated.price).toBe(99.99);
                expect(updated.isPrivate).toBe(!created.isPrivate);
                done();
              },
              error: err => {
                fail(`updateMatch failed: ${err.message}`);
                done();
              }
            });

          },
          error: err => {
            fail(`createMatch failed: ${err.message}`);
            done();
          }
        });
      }
    });

  });

  it('deleteMatch should remove a match when admin is logged in', (done: DoneFn) => {
    loginService.login(adminLoginRequest).subscribe({
      next: () => {
        service.createMatch(mockMatch).subscribe({
          next: (created: Match) => {
            expect(created).toBeTruthy();
            const id = created.id;

            service.deleteMatch(id!).subscribe({
              next: (response: Match) => {
                expect(response).toBeTruthy();
                expect(response.id).toBe(id);
                done();
              },
              error: err => {
                fail(`deleteMatch failed: ${err.message}`);
                done();
              }
            });
          },
          error: err => {
            fail(`createMatch failed: ${err.message}`);
            done();
          }
        });
      }
    });
  });

  it('addMatchReult should add result to a match when organizer is logged in', (done: DoneFn) => {
    const mockResult: MatchResult = {
      team1Name: "A",
      team2Name: "B",
      team1Score: 0,
      team2Score: 0,
      team1GamesPerSet: [6,6],
      team2GamesPerSet: [2,1]
    };
    loginService.login(loginRequest).subscribe({
      next: () => {
            const id = 6;
            service.addMatchResult(id!, mockResult).subscribe({
              next: (matchResult: MatchResult) => {
                expect(matchResult).toBeTruthy();
                expect(matchResult.team1GamesPerSet).toEqual(mockResult.team1GamesPerSet);
                expect(matchResult.team2GamesPerSet).toEqual(mockResult.team2GamesPerSet);
                done();
              },
              error: err => {
                fail(`addMatchResult failed: ${err.message}`);
                done();
              }
            });
          
      }
    });
  });
});