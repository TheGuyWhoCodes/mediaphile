import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {GreetingComponent} from "./pages/greeting/greeting.component";
import {SearchComponent} from "./pages/search/search.component";
import {BookDetailsComponent} from "./pages/book-details/book-details.component";
import {MovieDetailsComponent} from "./pages/movie-details/movie-details.component";
import {LoginComponent} from "./pages/login/login.component";
import {HomeComponent} from "./pages/home/home.component";
import {AuthGuardService} from "./auth/auth-guard.service";
import {QueueComponent} from "./pages/home/queue/queue.component";


const routes: Routes = [
  {
    path:'',
    component: GreetingComponent
  },
  {
    path:"search",
    component: SearchComponent
  },
  {
    path:"movie/:id",
    component: MovieDetailsComponent
  },
  {
    path:"book/:id",
    component: BookDetailsComponent
  },
  {
    path:"login",
    component: LoginComponent
  },
  {
    path: 'register',
    redirectTo : 'login',
    pathMatch : 'full'
  },
  {
    path:"home",
    component:HomeComponent,
    canActivate: [AuthGuardService]
  },
  {
    path:"queue",
    component: QueueComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [AuthGuardService]
})
export class AppRoutingModule { }
