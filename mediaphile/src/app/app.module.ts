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
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from "@angular/common/http";
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
import { ReviewComponent } from './pages/helper/review/review.component';
import { ReviewEntityComponent } from './pages/helper/review/review-entity/review-entity.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ReviewSubmitComponent } from './pages/helper/review-submit/review-submit.component';
import {HttpErrorInterceptor} from "./http.error.interceptor";
import {ModalComponent} from "./pages/helper/modal/modal.component";

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
    HomeEntityComponent,
    ReviewComponent,
    ReviewEntityComponent,
    ReviewSubmitComponent,
    ModalComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FontAwesomeModule,
    FormsModule,
    HttpClientModule,
    NgbModule,
  ],
  providers: [
    Title,
    InfoService,
    HttpClient,
    AuthGuardService,
    LoginStatus,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true
    },
    ModalComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
