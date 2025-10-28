import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatchComponent } from './match';
import { CommonModule } from '@angular/common';


describe('MatchComponent', () => {
  let component: MatchComponent;
  let fixture: ComponentFixture<MatchComponent>;
  let compiled: HTMLElement;

  const mockUser = {
    id: 1,
    realname: 'Carlos López',
    username: 'carlosl',
    email: 'carlos@example.com',
    birthdate: new Date('1990-05-14'),
    gender: 'M',
    description: 'Jugador apasionado',
    level: 5.5,
  }
  const mockMatch = {
    id: 1,
    date: new Date('2025-09-15T19:00:00Z'),
    type: true,
    isPrivate: false,
    state: true,
    organizer: mockUser,
    sport: 'Fútbol',
    price: Number(10),
    club: { city: 'Madrid', name: 'Club Deportivo Madrid',  address: 'Calle Falsa 123' },
    players: [mockUser],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonModule, MatchComponent],
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

    expect(title?.textContent).toContain('Fútbol');
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
    expect(price?.textContent).toContain('€');
  });
});
