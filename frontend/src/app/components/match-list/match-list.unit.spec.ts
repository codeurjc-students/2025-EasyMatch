import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { MatchListComponent } from './match-list';
import { MatchService } from '../../service/match.service';
import { MatchComponent } from '../match/match';
import { MatPaginatorModule } from '@angular/material/paginator';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from "@angular/router/testing";

class MockMatchService {
  getMatches() {
    return of({
      content: [
        {
          id: 1,
          date: new Date(),
          type: true,
          isPrivate: false,
          state: true,
          sport: {name: 'Futbol', modes: [{name: '7v7', playersPerGame: 14}]},
          organizer: {
            id: 1,
            realname: 'Carlos',
            username: 'carlosr',
            email: 'carlos@example.com',
            birthdate: new Date('1990-05-14'),
            gender: 'M',
            description: 'Apasionado del fútbol 7',
            level: 7,
          },
          club: {city: 'Valencia',name: '',address: ''},
          players: []
        },
        {
          id: 2,
          date: new Date(),
          type: false,
          isPrivate: false,
          state: true,
          sport: {name: 'Pádel', modes: [{name: 'Dobles', playersPerGame: 4}]},
          organizer: {
            id: 2,
            realname: 'Ana',
            username: 'anap',
            email: 'ana@example.com',
            birthdate: new Date('1993-03-12'),
            gender: 'F',
            description: 'Amante del pádel',
            level: 5,
          },
          club: {city: 'Madrid',name: '',address: ''},
          players: []
        },
      ],
      totalElements: 2,
      number: 0,
    });
  }
}

describe('MatchListComponent', () => {
  let fixture: ComponentFixture<MatchListComponent>;
  let component: MatchListComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        MatPaginatorModule,
        MatchComponent,
        MatchListComponent,
        HttpClientTestingModule,
        RouterTestingModule,
      ],
      providers: [{ provide: MatchService, useClass: MockMatchService }],
    }).compileComponents();

    fixture = TestBed.createComponent(MatchListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load matches from mock service', () => {
    const matches = component.matches();
    expect(matches.length).toBe(2);
    expect(matches[0].organizer.realname).toBe('Carlos');
    expect(component.totalElements()).toBe(2);
  });

  it('should update pagination when onPageChange is called', () => {
    const spy = spyOn(component, 'loadMatches');
    component.onPageChange({ pageIndex: 1, pageSize: 5, length: 10 } as any);
    expect(component.pageIndex).toBe(1);
    expect(component.pageSize).toBe(5);
    expect(spy).toHaveBeenCalledWith(1, 5);
  });
});