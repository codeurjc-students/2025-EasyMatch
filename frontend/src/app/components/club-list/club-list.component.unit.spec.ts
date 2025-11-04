import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ClubListComponent } from './club-list.component';
import { ClubService } from '../../service/club.service';
import { ClubComponent } from '../club/club.component';
import { MatPaginatorModule } from '@angular/material/paginator';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

class MockClubService {
  getClubs() {
    return of({
      content: [
        {
          id: 1,
          name: 'Club Deportivo Valencia',
          city: 'Valencia',
          address: 'Av. del Puerto 123',
          phone: "93492884",
          sports: [
            { name: 'Fútbol', modes: [{ name: '11v11', playersPerGame: 22 }] },
            { name: 'Tenis', modes: [{ name: 'Individual', playersPerGame: 2 }] }
          ],
          email: 'info@valenciaclub.com',
          schedule: {
            openingTime: "8:00",
            closingTime: "22:30"
          },
          priceRange: {
            minPrice: Number(10),
            maxPrice: Number(35),
            unit: "€/hora"
          }

        },
        {
          id: 2,
          name: 'Club Pádel Madrid',
          city: 'Madrid',
          address: 'Calle Mayor 45',
          phone: '943493219',
          sports: [
            { name: 'Pádel', modes: [{ name: 'Dobles', playersPerGame: 4 }] },
            { name: 'Squash', modes: [{ name: 'Individual', playersPerGame: 2 }] }
          ],
          email: 'contacto@padelmadrid.com',
          schedule: {
            openingTime: '9:00',
            closingTime: '23:30'
          },
          priceRange: {
            minPrice: Number(15),
            maxPrice: Number(30),
            unit: '€/hora'
          }
        }
      ],
      totalElements: 2,
      number: 0,
    });
  }
}

describe('ClubListComponent', () => {
  let fixture: ComponentFixture<ClubListComponent>;
  let component: ClubListComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        MatPaginatorModule,
        ClubComponent,
        ClubListComponent,
        HttpClientTestingModule,
        RouterTestingModule,
      ],
      providers: [{ provide: ClubService, useClass: MockClubService }],
    }).compileComponents();

    fixture = TestBed.createComponent(ClubListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load clubs from mock service', () => {
    const clubs = component.clubs();
    expect(clubs.length).toBe(2);
    expect(clubs[0].name).toBe('Club Deportivo Valencia');
    expect(component.totalElements()).toBe(2);
  });

  it('should update pagination when onPageChange is called', () => {
    const spy = spyOn(component, 'loadClubs');
    component.onPageChange({ pageIndex: 1, pageSize: 5, length: 10 } as any);
    expect(component.pageIndex).toBe(1);
    expect(component.pageSize).toBe(5);
    expect(spy).toHaveBeenCalledWith(1, 5);
  });
});
