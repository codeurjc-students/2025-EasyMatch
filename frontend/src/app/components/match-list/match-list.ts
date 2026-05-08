import { Component, OnInit, signal, ViewChild } from '@angular/core';
import { MatchService } from '../../service/match.service';
import { Match } from '../../models/match.model';
import { MatchComponent } from '../match/match';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from "../header/header.component";
import { MatchFilterComponent } from "../match-filter/match-filter.component";
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';


@Component({
  selector: 'app-match-list',
  templateUrl:'match-list.html',
  styleUrls: ['./match-list.scss'],
  imports: [MatchComponent, CommonModule, HeaderComponent, MatchFilterComponent, MatProgressSpinnerModule],
  standalone: true,
})
export class MatchListComponent implements OnInit {
  matches = signal<Match[]>([]);
  totalElements = signal(0);
  loading = signal(false);
  pageSize = 10;
  pageIndex = 0;

  filters = signal<{ search?: string; sport?: string; timeRange?: string, includeFriendlies?: boolean }>({});


  constructor(private matchService: MatchService) {}

  ngOnInit(): void {
    this.loadMatches(true);
  }

  hasMore(): boolean {
    return this.matches().length < this.totalElements();
  }

  onFiltersChanged(filters: { search?: string; sport?: string; timeRange?: string, includeFriendlies: boolean }): void {
    this.filters.set(filters);
    this.pageIndex = 0;
    this.loadMatches(true);
  }

  loadMatches(reset = false): void {
    this.loading.set(true);

    if (reset) {
      this.pageIndex = 0;
      this.matches.set([]);
    }

    this.matchService.getMatches(this.pageIndex, this.pageSize, 'date,asc', this.filters()).subscribe({
      next: (response) => {
        this.totalElements.set(response.totalElements);
        this.matches.update(prev => [
          ...prev,
          ...response.content
        ]);
        this.pageIndex = response.number + 1;
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando matches:', err);
        this.loading.set(false);
      },
    });
  }

  loadMore(): void {
    this.loadMatches();
  }

  
}
