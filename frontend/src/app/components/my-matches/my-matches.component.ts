import { Component, computed, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatchService } from '../../service/match.service';
import { UserService } from '../../service/user.service';
import { Match } from '../../models/match.model';
import { HeaderComponent } from '../header/header.component';
import { MatchComponent } from '../match/match';
import { MatIcon } from "@angular/material/icon";
import { MatDivider } from "@angular/material/divider";
import { MatCard } from "@angular/material/card";

@Component({
  selector: 'app-my-matches',
  standalone: true,
  imports: [CommonModule, HeaderComponent, MatchComponent, MatIcon, MatDivider, MatCard],
  templateUrl: './my-matches.component.html',
  styleUrls: ['./my-matches.component.scss']
})
export class MyMatchesComponent implements OnInit {

  matches = signal<Match[]>([]);
  loading = signal(true);

  constructor(
    private userService: UserService
  ) {}

  openMatches = computed(() =>
    this.matches().filter(m =>
      m.state === true
    )
  );

  closedMatches = computed(() =>
    this.matches().filter(m =>
      m.state === false
    )
  );

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.userService.getUserMatches(user!.id).subscribe({
          next: (data) => {
            this.matches.set(data);
            this.loading.set(false);
          },
          error: (err) => {
            console.error("Error cargando partidos:", err);
            this.loading.set(false);
          }
        });
      }
    });
  }
}
