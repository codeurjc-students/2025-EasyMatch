import { TestBed } from "@angular/core/testing";
import { MatchService } from "./match.service";
import { HttpClientModule, provideHttpClient, withInterceptors } from "@angular/common/http";
import { Sport } from "../models/sport.model";
import { Club } from "../models/club.model";
import { Match } from "../models/match.model";
import { LoginService } from "./login.service";
import { ScoringType } from "../models/scoring-type";
import { MatchResult } from "../models/match-result.model";
import { switchMap } from "rxjs/internal/operators/switchMap";
import { credentialsInterceptor } from "../interceptors/auth.interceptor";

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
      numberOfCourts: [2],
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

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientModule],
        providers: [MatchService, LoginService, provideHttpClient(withInterceptors([credentialsInterceptor]))]
      });

      service = TestBed.inject(MatchService);
      loginService = TestBed.inject(LoginService);
    });

    afterEach(() => {
      sessionStorage.clear();
    });

    function login(user = loginRequest) {
      return loginService.login(user);
    }

    
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
    login().pipe(
      switchMap(() => service.createMatch(mockMatch))
    ).subscribe(createdMatch => {

      expect(createdMatch).toBeTruthy();
      expect(createdMatch.organizer.realname).toBe('Pedro Garcia');
      expect(createdMatch.state).toBeTrue();
      expect(createdMatch.date instanceof Date).toBeTrue();

      done();
    });
   });

   it('joinMatch should add the logged user to the match\'s players', (done: DoneFn) => {
    const matchId = 5; 
    login().pipe(
      switchMap(() => service.joinMatch(matchId, 'A'))
    ).subscribe(response => {

      expect(response).toBeTruthy();
      expect(response.status).toContain('SUCCESS');
      expect(response.message).toContain('Player added to team A');

      done();
    });
   });

   it('leaveMatch should remove the logged user from the match\'s players', (done: DoneFn) => {
    const matchId = 5;
    login().pipe(
      switchMap(() => service.leaveMatch(matchId))
    ).subscribe(response => {

      expect(response).toBeTruthy();
      expect(response.status).toContain('SUCCESS');
      expect(response.message).toContain('Player removed from match');

      done();
    });

   });

   it('updateMatch should update match fields when admin is logged in', (done: DoneFn) => {
    login(adminLoginRequest).pipe(

      switchMap(() => service.createMatch(mockMatch)),

      switchMap((created: Match) => {

        const updatedMatch: Partial<Match> = {
          ...created,
          price: 99.99,
          isPrivate: !created.isPrivate
        };

        return service.updateMatch(created.id!, updatedMatch);
      })

    ).subscribe(updated => {

      expect(updated).toBeTruthy();
      expect(updated.price).toBe(99.99);

      done();
    });


  });

  it('deleteMatch should remove a match when admin is logged in', (done: DoneFn) => {
    let createdId: number;

    login(adminLoginRequest).pipe(

      switchMap(() => service.createMatch(mockMatch)),

      switchMap((created: Match) => {
        createdId = created.id!;
        return service.deleteMatch(createdId);
      })

    ).subscribe(response => {

      expect(response).toBeTruthy();
      expect(response.id).toBe(createdId);

      done();
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
    const matchId = 6;

    login().pipe(
      switchMap(() => service.addMatchResult(matchId, mockResult))
    ).subscribe(matchResult => {

      expect(matchResult).toBeTruthy();
      expect(matchResult.team1GamesPerSet).toEqual(mockResult.team1GamesPerSet);
      expect(matchResult.team2GamesPerSet).toEqual(mockResult.team2GamesPerSet);

      done();
    });

  });
});