import { Component, OnInit } from '@angular/core';
import {InfoService} from "../../info.service";
import {LoginStatus} from "../../auth/login.status";
import {ActivatedRoute} from "@angular/router";
import {Title} from "@angular/platform-browser";
import {Observable} from "rxjs";

@Component({
  selector: 'app-book-details',
  templateUrl: './book-details.component.html',
  styleUrls: ['./book-details.component.scss']
})
export class BookDetailsComponent implements OnInit {

  private userId: string;
  private id: string;
  public hasResults: boolean;
  public entity: {};
  public queue: Observable<any>;
  public watched: Observable<any>;

  constructor(private infoSvc: InfoService, public loginStatus: LoginStatus, private route: ActivatedRoute, private title: Title) { }

  ngOnInit(): void {
    this.loginStatus.sharedAccountId.subscribe(x => {
      this.userId = x;
      this.queue = this.infoSvc.getQueue(this.userId, "queue")
      this.watched = this.infoSvc.getQueue(this.userId, "viewed")
    })
    if (this.route.snapshot.paramMap.get("id") != undefined) {
      this.id = this.route.snapshot.paramMap.get("id");
    }
    if(this.id != null){
      this.infoSvc.getBookDetails(this.id).subscribe(data => {
        this.entity = data;
        this.title.setTitle(`Mediaphile Listing for "${data['volumeInfo']["title"]}"`)
        this.hasResults = true;
      });
    }
  }

  public getEntityImageUrl() : string {
    return "assets/placeholder.jpeg"
  }

  public getEntityPosterUrl(): string {
    if("imageLinks" in this.entity["volumeInfo"]) {
      return this.entity["volumeInfo"]["imageLinks"]["thumbnail"]
    }
    return "assets/poster-placeholder.png"
  }


  public addToQueuedList() {
    this.infoSvc.postQueue(
      this.getEntityPosterUrl(),
      this.id,
      "book",
      this.entity["volumeInfo"]["title"],
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
      this.getEntityPosterUrl(),
      this.id,
      "book",
      this.entity["volumeInfo"]["title"],
      "viewed",
      this.userId
    ).subscribe(x => {
      if(x["success"]) {
        alert("Successfully added to watched list!");
      }
    })
  }
}
