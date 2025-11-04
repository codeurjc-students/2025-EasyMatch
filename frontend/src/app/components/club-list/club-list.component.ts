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
    this.loadClubs();
  }

  loadClubs(page = 0, size = this.pageSize): void {
    this.loading.set(true);
    this.clubService.getClubs(page, size, 'date,asc', this.filters()).subscribe({
      next: (response) => {
        this.clubs.set(response.content);
        this.totalElements.set(response.totalElements);
        this.pageIndex = response.number;
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando clubes:', err);
        this.loading.set(false);
      },
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.loadClubs(this.pageIndex, this.pageSize);
  }

  onFiltersChanged(filters: { search?: string; sport?: string; city?: string }): void {
    this.filters.set(filters);
    this.pageIndex = 0;
    this.loadClubs(0, this.pageSize);
  }
}