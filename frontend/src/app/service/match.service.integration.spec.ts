import { TestBed } from "@angular/core/testing";
import { MatchService } from "./match.service";
import { HttpClientModule } from "@angular/common/http";
import { User } from "../models/user.model";
import { Sport } from "../models/sport.model";
import { Club } from "../models/club.model";
import { Match } from "../models/match.model";
import { LoginService } from "./login.service";
import { LoginRequest } from "../models/auth/login-request.model";

jasmine.DEFAULT_TIMEOUT_INTERVAL = 15000;

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
            expect(response.totalElements).toBe(4)
            done();
        });
    });

   it('createMatch should send a valid payload and return the created match', (done: DoneFn) => {
    const mockSport: Sport = {
      id: 1,
      name: 'Fútbol',
      modes: [{ name: '7v7', playersPerGame: 14 }],
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

    loginService.login(loginRequest).subscribe();
    service.createMatch(mockMatch).subscribe({
      next: (createdMatch) => {
        expect(createdMatch).toBeTruthy();
        expect(createdMatch.organizer.realname).toBe('Pedro Garcia');
        expect(createdMatch.players.length).toBe(1);
        expect(createdMatch.state).toBeTrue();
        expect(createdMatch.date instanceof Date).toBeTrue();
        done();
      },
      error: (err) => {
        fail(`createMatch failed: ${err.message}`);
        done();
      },
    });
  });
});