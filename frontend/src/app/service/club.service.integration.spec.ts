import { TestBed } from "@angular/core/testing";
import { HttpClientModule } from "@angular/common/http";
import { ClubService } from "./club.service";

jasmine.DEFAULT_TIMEOUT_INTERVAL = 15000;

describe('ClubService', () => {
  let service: ClubService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [ClubService],
    });
    service = TestBed.inject(ClubService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it("getClubs should return API's value as an Observable", (done: DoneFn) => {
    service.getClubs(0, 10).subscribe(response => {
      expect(response).toBeTruthy();
      expect(Array.isArray(response.content)).toBeTrue();
      expect(response.content.length).toBeGreaterThan(0);
      expect(response.totalElements).toBe(5);
      done();
    });
  });

  it("getClub should return a single club by id", (done: DoneFn) => {
    const testClubId = 1;
    service.getClub(testClubId).subscribe(response => {
      expect(response).toBeTruthy();
      expect(response.id).toBe(testClubId);
      expect(response.name).toBe("Tennis Club Elite");
      expect(response.city).toBe("Madrid");
      done();
    });
  });
});
