import { Component, OnInit } from '@angular/core';
import { MatchService } from '../../service/match.service';
import { Match } from '../../models/match.model';


@Component({
  selector: 'app-match-list',
  templateUrl:'match-list.html',
  standalone: false,
})
export class MatchListComponent implements OnInit {
  matches: Match[] = [];

  constructor(private matchService: MatchService) {}

  ngOnInit() {
    this.matchService.getMatches().subscribe(data => {
		console.log("Datos recibidos:",data);
      this.matches = data;

    });
  }
}