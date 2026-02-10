import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app';

import { MatchListComponent } from "./components/match-list/match-list";
import { LoginComponent } from './components/login/login.component';
import { MatchComponent } from './components/match/match';

@NgModule({
  imports: [
    BrowserModule,
    NgxChartsModule,
    HttpClientModule,
    MatchComponent,
    MatchListComponent,
    LoginComponent,
    AppComponent
  ],
  providers: [],
})
export class AppModule { }