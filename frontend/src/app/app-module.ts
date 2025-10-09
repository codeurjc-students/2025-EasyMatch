import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app';
import { MatchComponent } from './components/match/match';
import { MatchListComponent } from "./components/match-list/match-list";

@NgModule({
  declarations: [
    AppComponent,
    MatchComponent,
    MatchListComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
