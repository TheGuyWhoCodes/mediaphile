import { Component, OnInit } from '@angular/core';
import {InfoService} from "../../info.service";
import {ActivatedRoute} from "@angular/router";
import {LoginStatus} from "../../auth/login.status";

@Component({
  selector: 'app-movie-details',
  templateUrl: './movie-details.component.html',
  styleUrls: ['./movie-details.component.scss']
})
export class MovieDetailsComponent implements OnInit {

  constructor(public loginStatus: LoginStatus, private infoSvc: InfoService, private route: ActivatedRoute) { }
  private id: string;
  private userId: string;
  public entity: {};

  public hasResults: boolean;

  ngOnInit(): void {
    this.loginStatus.sharedAccountId.subscribe(x => {
      this.userId = x;
    })
    if (this.route.snapshot.paramMap.get("id") != undefined) {
      this.id = this.route.snapshot.paramMap.get("id");
    }
    if(this.id != null){
      this.infoSvc.getMovieDetails(this.id).subscribe(data => {
        this.entity = data;
        this.hasResults = true;
      });
    }
  }

  public getEntityImageUrl() : string {
    if(this.entity["backdropPath"]) {
      return "https://image.tmdb.org/t/p/original" + this.entity['backdropPath']
    }
    return "assets/placeholder.jpeg"
  }

  public getEntityPosterUrl(): string {
      if(this.entity["posterPath"]) {
        return "https://image.tmdb.org/t/p/w500" + this.entity['posterPath']
      }
    return "assets/poster-placeholder.png"
  }

  public addToQueuedList() {
    this.infoSvc.postQueue(
      this.entity["posterPath"],
      this.id,
      "movie",
      this.entity["title"],
      "queue",
      this.userId
    ).subscribe(x => {
        if(x["success"]) {
          alert("Successfully added to queue!");
        }
    })
  }

  public addToWatchedList() {
    this.infoSvc.postQueue(
      this.entity["posterPath"],
      this.id,
      "movie",
      this.entity["title"],
      "viewed",
      this.userId
    ).subscribe(x => {
      if(x["success"]) {
        alert("Successfully added to watched list!");
      }
    })
  }
}
