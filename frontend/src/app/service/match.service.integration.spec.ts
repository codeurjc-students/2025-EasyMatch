import { TestBed } from "@angular/core/testing";
import { MatchService } from "./match.service";
import { HttpClientModule } from "@angular/common/http";
import { Sport } from "../models/sport.model";
import { Club } from "../models/club.model";
import { Match } from "../models/match.model";
import { LoginService } from "./login.service";
import { ScoringType } from "../models/scoring-type";

jasmine.DEFAULT_TIMEOUT_INTERVAL = 15000;
jasmine.getEnv().configure({ random: false });

describe('MatchService', () => {
    let service : MatchService;
    let loginService : LoginService;
    let loginRequest = {
        username: 'pedro@emeal.com',    
        password: 'pedroga4'
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

});