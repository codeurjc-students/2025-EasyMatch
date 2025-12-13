import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClubComponent } from './club.component';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { ScoringType } from '../../models/scoring-type';

describe('ClubComponent', () => {
  let component: ClubComponent;
  let fixture: ComponentFixture<ClubComponent>;
  let compiled: HTMLElement;

  const mockClub = {
    id: 1,
    name: 'Club Deportivo Madrid',
    city: 'Madrid',
    address: 'Calle Falsa 123',
    sports: [
      { name: 'Fútbol', modes: [{ name: '7v7', playersPerGame: 14 }], scoringType: ScoringType.SETS },
      { name: 'Tenis', modes: [{ name: 'Individual', playersPerGame: 2 }], scoringType: ScoringType.SCORE },
    ],
    schedule: { openingTime: '09:00', closingTime: '22:00' },
    priceRange: { minPrice: 8, maxPrice: 15, unit: '€/hora' },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        MatIconModule,
        MatCardModule,
        ClubComponent,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ClubComponent);
    component = fixture.componentInstance;
    component.club = mockClub;
    fixture.detectChanges();
    compiled = fixture.nativeElement;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should render the club name and city', () => {
    const name = compiled.querySelector('#club-name');
    const city = compiled.querySelector('.club-meta p');

    expect(name?.textContent).toContain('Club Deportivo Madrid');
    expect(city?.textContent).toContain('Madrid');
  });

  it('should display opening and closing hours', () => {
    const schedule = compiled.querySelector('.club-meta p:nth-child(2)');
    expect(schedule?.textContent).toContain('09:00');
    expect(schedule?.textContent).toContain('22:00');
  });

  it('should render all sports offered by the club', () => {
    const sports = compiled.querySelectorAll('.sport');
    expect(sports.length).toBe(2);
    expect(sports[0].textContent).toContain('Fútbol');
    expect(sports[1].textContent).toContain('Tenis');
  });

  it('should display the correct price range and unit', () => {
    const price = compiled.querySelector('.price');
    expect(price?.textContent).toContain('8');
    expect(price?.textContent).toContain('15');
    expect(price?.textContent).toContain('€/hora');
  });

  it('should render buttons for availability and details', () => {
    const buttons = compiled.querySelectorAll('button');
    expect(buttons.length).toBe(2);
    expect(buttons[0].textContent).toContain('Disponibilidad');
    expect(buttons[1].textContent).toContain('Ver detalles');
  });

});
