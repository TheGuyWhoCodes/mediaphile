import { Component, OnInit } from '@angular/core';
import {InfoService} from "../../info.service";
import {ActivatedRoute} from "@angular/router";
import {LoginStatus} from "../../auth/login.status";
import {faMinusCircle} from "@fortawesome/free-solid-svg-icons";
import {Observable} from "rxjs";
import {Title} from "@angular/platform-browser";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {ModalComponent} from "../helper/modal/modal.component";

@Component({
  selector: 'app-movie-details',
  templateUrl: './movie-details.component.html',
  styleUrls: ['./movie-details.component.scss']
})
export class MovieDetailsComponent implements OnInit {


  public miniusCircle = faMinusCircle;

  public userId: string;
  public movieId: string;

  public entity: Observable<any>;
  public movieData: {};

  public hasQueue: boolean;
  public hasWatched: boolean;
  public hasListResponse: boolean = false;

  public isLoggedIn: boolean;
  constructor(private infoSvc: InfoService, public loginStatus: LoginStatus, private route: ActivatedRoute, private title: Title, private modalService: NgbModal) { }

  ngOnInit(): void {
    this.loginStatus.sharedAccountId.subscribe(x => {
      this.userId = x;
      if(this.userId != "") {
        this.infoSvc.isInList(this.userId, this.movieId).subscribe(data => {
          this.hasQueue = data.isInQueue;
          this.hasWatched = data.isInViewed;
          this.hasListResponse = true;
        })
      }
    })

    this.loginStatus.sharedStatus.subscribe(status => {
      this.isLoggedIn = status;
    })

    if (this.route.snapshot.paramMap.get("id") != undefined) {
      this.movieId = this.route.snapshot.paramMap.get("id");
    }
    if(this.movieId != null){
      this.entity = this.infoSvc.getMovieDetails(this.movieId);
      this.entity.subscribe(data => {
        this.movieData = data;
        this.title.setTitle(`Mediaphile Listing for "${data["title"]}"`)
      });
    }
  }

  public getEntityImageUrl() : string {
    if(this.movieData && this.movieData["backdropPath"]) {
      return "https://image.tmdb.org/t/p/original" + this.movieData['backdropPath']
    }
    return "assets/placeholder.jpeg"
  }

  public getEntityPosterUrl(): string {
    if(this.movieData && this.movieData["posterPath"]) {
      return "https://image.tmdb.org/t/p/w500" + this.movieData['posterPath']
    }
    return "assets/poster-placeholder.png"
  }


  public addToQueuedList() {
    this.infoSvc.postQueue(
      this.getEntityPosterUrl(),
      this.movieId,
      "movie",
      this.movieData["title"],
      "queue",
      this.userId
    ).subscribe(x => {
      this.hasQueue = true;
      if(x["success"]) {
        this.showMessage("Success!", "Successfully added to queue!");
      }
    }, error => {
      this.showMessage("Oops!", "Unable to add movie to queue, try again later!");
    })
  }

  public addToWatchedList() {
    this.infoSvc.postQueue(
      this.getEntityPosterUrl(),
      this.movieId,
      "movie",
      this.movieData["title"],
      "viewed",
      this.userId
    ).subscribe(x => {
      this.hasWatched = true;
      if(x["success"]) {
        this.showMessage("Success!", "Successfully added to watched list!");
      }
    }, error => {
      this.showMessage("Oops!", "Unable to add movie to watched list, try again later!");
    })
  }

  public removeFromList(listType: string) {
    this.infoSvc.deleteFromQueue(listType, "movie", this.movieId).subscribe(data => {
      if(listType == "queue") {
        this.hasQueue = false;
      } else {
        this.hasWatched = false;
      }
      this.showMessage("Success!", "Deleted movie successfully!");
    }, error => {
      this.showMessage("Oops!", "Unable to delete movie from list, try again later!");
    })
  }

  public isItemInList(list: {}[], mediaId: string) {
    return (list.some(function(el) {
      return el["mediaId"] === mediaId;
    }));
  }

  scroll(el: HTMLElement) {
    el.scrollIntoView();
  }

  public showMessage(title: string, message: string) {
    const modalRef = this.modalService.open(ModalComponent);
    modalRef.componentInstance.title = title
    modalRef.componentInstance.message = message
  }
}
