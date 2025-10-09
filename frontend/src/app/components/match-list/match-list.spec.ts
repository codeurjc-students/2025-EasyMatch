import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatchListComponent } from "./match-list";
import { MatchComponent } from "../match/match";
import { MatchService } from "../../service/match.service";
import { of } from "rxjs";

// Client Unitary Test
class MockMatchService {
  getMatches() {
    return of([
      { organizer: 'Carlos', sport: 'Fútbol', date: new Date(), type: true, isPrivate: false, state: true },
      { organizer: 'Ana', sport: 'Pádel', date: new Date(), type: false, isPrivate: false, state: true }
    ]);
  }
}
describe('MatchListComponent', () => {
  let fixture: ComponentFixture<MatchListComponent>;
  let component: MatchListComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MatchListComponent, MatchComponent],
      providers: [{ provide: MatchService, useClass: MockMatchService }]
    }).compileComponents();

    fixture = TestBed.createComponent(MatchListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load matches from mock service', () => {
    expect(component.matches.length).toBe(2);
    expect(component.matches[0].organizer).toBe('Carlos');
  });
});