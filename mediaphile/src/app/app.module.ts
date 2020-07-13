import {BrowserModule, Title} from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {HeaderComponent} from "./header/header.component";
import {FooterComponent} from "./footer/footer.component";
import { GreetingComponent } from './pages/greeting/greeting.component';
import {FormsModule} from "@angular/forms";
import { SearchComponent } from './pages/search/search.component';
import {InfoService} from "./info.service";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import { ResultsComponent } from './pages/search/results/results.component';
import { EntityComponent } from './pages/search/entity/entity.component';
import { BookDetailsComponent } from './pages/book-details/book-details.component';
import { MovieDetailsComponent } from './pages/movie-details/movie-details.component';
import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import {AuthGuardService} from "./auth/auth-guard.service";
import {LoginStatus} from "./auth/login.status";
import { QueueComponent } from './pages/home/queue/queue.component';
import { HomeEntityComponent } from './pages/home/home-entity/home-entity.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    GreetingComponent,
    SearchComponent,
    ResultsComponent,
    EntityComponent,
    BookDetailsComponent,
    MovieDetailsComponent,
    LoginComponent,
    HomeComponent,
    QueueComponent,
    HomeEntityComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FontAwesomeModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [
    Title,
    InfoService,
    HttpClient,
    AuthGuardService,
    LoginStatus
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
