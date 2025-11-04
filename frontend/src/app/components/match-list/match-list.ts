import { Component, OnInit, signal, ViewChild } from '@angular/core';
import { MatchService } from '../../service/match.service';
import { Match } from '../../models/match.model';
import { MatchComponent } from '../match/match';
import { CommonModule } from '@angular/common';
import { MatPaginator, PageEvent, MatPaginatorModule } from '@angular/material/paginator';
import { HeaderComponent } from "../header/header.component";
import { MatchFilterComponent } from "../match-filter/match-filter.component";
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';


@Component({
  selector: 'app-match-list',
  templateUrl:'match-list.html',
  styleUrls: ['./match-list.scss'],
  imports: [MatchComponent, CommonModule, MatPaginator, HeaderComponent, MatchFilterComponent, MatProgressSpinnerModule],
  standalone: true,
})
export class MatchListComponent implements OnInit {
  matches = signal<Match[]>([]);
  totalElements = signal(0);
  loading = signal(false);
  pageSize = 10;
  pageIndex = 0;

  filters = signal<{ search?: string; sport?: string; timeRange?: string, includeFriendlies?: boolean }>({});

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private matchService: MatchService) {}

  ngOnInit(): void {
    this.loadMatches();
  }

  onFiltersChanged(filters: { search?: string; sport?: string; timeRange?: string, includeFriendlies: boolean }): void {
    this.filters.set(filters);
    this.pageIndex = 0;
    this.loadMatches(0, this.pageSize);
  }

  loadMatches(page: number = 0, size: number = this.pageSize): void {
    this.loading.set(true);
    this.matchService.getMatches(page, size,'date,asc', this.filters()).subscribe({
      next: (response) => {
        this.matches.set(response.content);
        this.totalElements.set(response.totalElements);
        this.pageIndex = response.number;
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando matches:', err);
        this.loading.set(false);
      },
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.loadMatches(this.pageIndex, this.pageSize);
  }

  
}
