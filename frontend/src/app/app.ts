import { Component, OnInit } from '@angular/core';
import { Match } from './match.model';
import { MatchService } from './match.service';
import { MatchComponent } from "./match";

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
})
export class AppComponent implements OnInit {
  matches: Match[] = [];

  constructor(private matchService: MatchService) {}

  ngOnInit() {
    this.matchService.getMatches().subscribe(data => {
		console.log("Datos recibidos:",data);
      this.matches = data;

    });
  }
}