import { TestBed } from "@angular/core/testing";

import { MatchService } from "./match.service";
import { HttpClientModule } from "@angular/common/http";

// Client Integration Test
jasmine.DEFAULT_TIMEOUT_INTERVAL = 15000;
describe('MatchService', () => {
    let service : MatchService;

    beforeEach(() =>{
        TestBed.configureTestingModule({imports:[HttpClientModule]});
        service = TestBed.inject(MatchService);
    });
    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('getMatches should return API\'s value as an Observable', (done: DoneFn) => {
        service.getMatches().subscribe(value => {
            expect(value.length).toBeGreaterThan(0);
            expect(value.length).toBe(4)
            done();
        });
        
        
    });
});