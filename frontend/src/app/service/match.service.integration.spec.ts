import { TestBed } from "@angular/core/testing";
import { MatchService } from "./match.service";
import { HttpClientModule } from "@angular/common/http";

jasmine.DEFAULT_TIMEOUT_INTERVAL = 15000;

describe('MatchService', () => {
    let service : MatchService;

    beforeEach(() =>{
        TestBed.configureTestingModule({imports:[HttpClientModule], providers: [MatchService]});
        service = TestBed.inject(MatchService);
    });
    
    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('getMatches should return API\'s value as an Observable', (done: DoneFn) => {
        service.getMatches(0, 10).subscribe(response => {
            expect(response).toBeTruthy();
            expect(response.content.length).toBeGreaterThan(0);
            expect(response.content.length).toBe(4)
            done();
        });
    });
});