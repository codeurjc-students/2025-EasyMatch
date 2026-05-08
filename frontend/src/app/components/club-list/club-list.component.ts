import { Component, OnInit, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { ClubComponent } from '../club/club.component';
import { Club } from '../../models/club.model';
import { ClubService } from '../../service/club.service';
import { HeaderComponent } from '../header/header.component';
import { ClubFilterComponent } from '../club-filter/club-filter.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-club-list',
  standalone: true,
  templateUrl: './club-list.component.html',
  styleUrls: ['./club-list.component.scss'],
  imports: [
    CommonModule,
    ClubComponent,
    MatPaginatorModule,
    HeaderComponent,
    ClubFilterComponent,
    MatProgressSpinnerModule,
  ],
})
export class ClubListComponent implements OnInit {
  clubs = signal<Club[]>([]);
  totalElements = signal(0);
  loading = signal(false);
  pageSize = 10;
  pageIndex = 0;

  filters = signal<{ search?: string; sport?: string; city?: string }>({});

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private clubService: ClubService) {}

  ngOnInit(): void {
    this.loadClubs(true);
  }

  hasMore(): boolean {
    return this.clubs().length < this.totalElements();
  }

  loadClubs(reset = false): void {
    this.loading.set(true);

    if (reset) {
      this.pageIndex = 0;
      this.clubs.set([]);
    }

    this.clubService.getClubs(this.pageIndex, this.pageSize, 'date,asc', this.filters()).subscribe({
      next: (response) => {
        this.totalElements.set(response.totalElements);
        this.clubs.update(prev => [
          ...prev,
          ...response.content
        ]);
        this.pageIndex = response.number + 1;
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando clubes:', err);
        this.loading.set(false);
      },
    });
  }


  onFiltersChanged(filters: { search?: string; sport?: string; city?: string }): void {
    this.filters.set(filters);
    this.pageIndex = 0;
    this.loadClubs(true);
  }

  loadMore(): void {
    this.loadClubs();
  }
}