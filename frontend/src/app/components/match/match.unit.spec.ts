import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatchComponent } from './match';
import { CommonModule } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { ScoringType } from '../../models/scoring-type';
import { LevelHistory } from '../../models/level-history.model';


describe('MatchComponent', () => {
  let component: MatchComponent;
  let fixture: ComponentFixture<MatchComponent>;
  let compiled: HTMLElement;

  const mockUser = {
    id: 1,
    realname: 'Carlos López',
    username: 'carlosl',
    email: 'carlos@example.com',
    birthDate: new Date('1990-05-14'),
    gender: true,
    description: 'Jugador apasionado',
    level: 5.5,
    stats: {
        totalMatches: 0,
        wins: 0,
        draws: 0,
        losses: 0,
        winRate: 0
    },
    levelHistory: [{date: new Date('2024-01-01'), levelBefore: 5.0, levelAfter: 5.5, won: true}],
    roles: [] 
  }

  const mockClub =  { 
      id: 1,
      city: 'Madrid', 
      name: 'Club Deportivo Madrid',  
      address: 'Calle Falsa 123',
      sports: [{ name: 'Futbol', modes: [{name: "7v7", playersPerGame: 14}], scoringType: ScoringType.SCORE}],
      schedule: { openingTime: '09:00', closingTime: '22:00' },
      priceRange: { minPrice: 8, maxPrice: 15, unit: '€/hora' }
  }
  const mockMatch = {
    id: 1,
    date: new Date('2025-09-15T19:00:00Z'),
    type: true,
    isPrivate: false,
    state: true,
    modeSelected: 0,
    organizer: mockUser,
    sport: {
      name: 'Futbol',
      modes: [{ name: '7v7', playersPerGame: 14 }],
      scoringType: ScoringType.SCORE
    },
    price: Number(10),
    club: mockClub,
    result : {
      team1Name: '',
      team2Name: '',
      team1Score: 0,
      team2Score: 0,
      team1Sets: 0,
      team2Sets: 0,
      team1GamesPerSet: [],
      team2GamesPerSet: []
    },
    team1Players: [mockUser],
    team2Players: []
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonModule, MatchComponent],
      providers:  [provideHttpClient()]
    }).compileComponents();

    fixture = TestBed.createComponent(MatchComponent);
    component = fixture.componentInstance;
    component.match = mockMatch;
    fixture.detectChanges();
    compiled = fixture.nativeElement; 
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should render sport name and city', () => {
    
    const title = compiled.querySelector('.sport-title');
    const city = compiled.querySelector('.match-city');

    expect(title?.textContent).toContain('Futbol');
    expect(city?.textContent).toContain('Madrid');
  });

  it('should display the organizer name and level', () => {
    const organizer = compiled.querySelector('.organizer-name');
    const level = compiled.querySelector('.organizer-level');

    expect(organizer?.textContent).toContain('Carlos López');
    expect(level?.textContent).toContain('5.5');
  });

  it('should display the price formatted in euros', () => {
    const price = compiled.querySelector('.match-price');
    expect(price?.textContent).toContain('10.00 €');
  });
});
