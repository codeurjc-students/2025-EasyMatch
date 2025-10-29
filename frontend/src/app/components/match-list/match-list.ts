import { Component, OnInit, signal, ViewChild,ViewEncapsulation } from '@angular/core';
import { MatchService } from '../../service/match.service';
import { Match } from '../../models/match.model';
import { MatchComponent } from '../match/match';
import { CommonModule } from '@angular/common';
import { MatPaginator, PageEvent, MatPaginatorModule } from '@angular/material/paginator';
import { HeaderComponent } from "../header/header.component";


@Component({
  selector: 'app-match-list',
  templateUrl:'match-list.html',
  styleUrls: ['./match-list.scss'],
  imports: [MatchComponent, CommonModule, MatPaginator, HeaderComponent],
  standalone: true,
})
export class MatchListComponent implements OnInit {
  matches = signal<Match[]>([]);
  totalElements = signal(0);
  pageSize = 10;
  pageIndex = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private matchService: MatchService) {}

  ngOnInit(): void {
    this.loadMatches();
  }

  loadMatches(page: number = 0, size: number = this.pageSize): void {
    this.matchService.getMatches(page, size).subscribe({
      next: response => {
        this.matches.set(response.content);
        this.totalElements.set(response.totalElements);
        this.pageIndex = response.number;
      },
      error: err => console.error('Error cargando partidos:', err),
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.loadMatches(this.pageIndex, this.pageSize);
  }
}