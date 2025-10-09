import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatchComponent } from './match';
import { Match } from '../../models/match.model';
import { By } from '@angular/platform-browser';


describe('MatchComponent', () => {
  let component: MatchComponent;
  let fixture: ComponentFixture<MatchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MatchComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(MatchComponent);
    component = fixture.componentInstance;
  });

  it('should create the match component', () => {
    expect(component).toBeTruthy();
  });

  it('should render the match data correctly', () => {
    // We simulate @Input
    const mockMatch: Match = {
      date: new Date('2025-10-10T18:30:00'),
      type: true,
      isPrivate: false,
      state: true,
      organizer: "Carlos Gómez",
      sport: "Fútbol"
    };

    component.match = mockMatch;

    // Triggers changes detection to update the virtual DOM
    fixture.detectChanges();

    // Now, we verify the rendered HTML
    const title = fixture.debugElement.query(By.css('h3')).nativeElement;

    expect(title.textContent).toContain('Carlos Gómez - Fútbol');
    
  });
});
