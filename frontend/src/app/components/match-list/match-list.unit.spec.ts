import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatchListComponent } from "./match-list";
import { MatchComponent } from "../match/match";
import { MatchService } from "../../service/match.service";
import { of } from "rxjs";

class MockMatchService {
  getMatches() {
    return of([
      { id: 1, date: new Date(), type: true, isPrivate: false, state: true, organizer: {
          id: 1,
          realname: 'Carlos',
          username: 'carlosr',
          email: 'carlos@example.com',
          birthdate: new Date('1990-05-14'),
          gender: 'M',
          description: 'Apasionado del fútbol 7',
          level: 7
        }, sport: 'Fútbol'},
      { id: 2, date: new Date(), type: false, isPrivate: false, state: true, organizer: 'Ana', sport: 'Pádel'}
    ]);
  }
}
describe('MatchListComponent', () => {
  let fixture: ComponentFixture<MatchListComponent>;
  let component: MatchListComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatchListComponent, MatchComponent],
      providers: [{ provide: MatchService, useClass: MockMatchService }]
    }).compileComponents();

    fixture = TestBed.createComponent(MatchListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load matches from mock service', () => {
    expect(component.matches.length).toBe(2);
    expect(component.matches[0].organizer.realname).toBe('Carlos');
  });
});