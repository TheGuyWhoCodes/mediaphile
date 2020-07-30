import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {InfoService} from "../../info.service";
import {LoginStatus} from "../../auth/login.status";
import {ActivatedRoute} from "@angular/router";
import {Title} from "@angular/platform-browser";
import {Observable} from "rxjs";
import {faMinusCircle} from "@fortawesome/free-solid-svg-icons";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {ModalComponent} from "../helper/modal/modal.component";
import {faAmazon} from "@fortawesome/free-brands-svg-icons";

@Component({
  selector: 'app-book-details',
  templateUrl: './book-details.component.html',
  styleUrls: ['./book-details.component.scss']
})
export class BookDetailsComponent implements OnInit {


  public miniusCircle = faMinusCircle;
  public faAmazon = faAmazon

  public userId: string;
  public bookId: string;

  public entity: Observable<any>;
  public bookData: {};

  public hasQueue: boolean;
  public hasWatched: boolean;
  public hasListResponse: boolean = false;

  public isLoggedIn: boolean;

  constructor(private infoSvc: InfoService, public loginStatus: LoginStatus, private route: ActivatedRoute, private title: Title, private modalService: NgbModal) { }

  ngOnInit(): void {
    this.loginStatus.sharedAccountId.subscribe(x => {
      this.userId = x;
      if(x != "") {
        if (this.userId != "") {
          this.infoSvc.isInList(this.userId, this.bookId).subscribe(data => {
            this.hasQueue = data.isInQueue;
            this.hasWatched = data.isInViewed;
            this.hasListResponse = true;
          })
        }
      }
    })

    this.loginStatus.sharedStatus.subscribe(status => {
      this.isLoggedIn = status;
    })

    if (this.route.snapshot.paramMap.get("id") != undefined) {
      this.bookId = this.route.snapshot.paramMap.get("id");
    }
    if(this.bookId != null){
      this.entity = this.infoSvc.getBookDetails(this.bookId);
      this.entity.subscribe(data => {
        this.bookData = data;
        this.title.setTitle(`Mediaphile Listing for "${data['volumeInfo']["title"]}"`)
      });
    }
  }

  public getEntityTitle() : string {
    return (this.bookData) ? this.bookData['volumeInfo']['title'] : "";
  }

  public getEntityImageUrl() : string {
    return "assets/placeholder.jpeg"
  }

  public getEntityPosterUrl(): string {
    if(this.bookData && "imageLinks" in this.bookData["volumeInfo"]) {
      return this.infoSvc.toHttps(this.bookData["volumeInfo"]["imageLinks"]["thumbnail"]);
    }
    return "assets/poster-placeholder.png"
  }


  public addToQueuedList() {
    this.infoSvc.postQueue(
      this.getEntityPosterUrl(),
      this.bookId,
      "book",
      this.bookData["volumeInfo"]["title"],
      "queue",
      this.userId
    ).subscribe(x => {
      this.hasQueue = true;
      if(x["success"]) {
        this.showMessage("Success!", "Successfully added to queue!");
      }
    }, error => {
      this.showMessage("Oops!", "Unable to add book to queue, try again later!");
    })
  }

  public addToWatchedList() {
    this.infoSvc.postQueue(
      this.getEntityPosterUrl(),
      this.bookId,
      "book",
      this.bookData["volumeInfo"]["title"],
      "viewed",
      this.userId
    ).subscribe(x => {
      this.hasWatched = true;
      if(x["success"]) {
        this.showMessage("Success!", "Successfully added to read list!");
      }
    }, error => {
      this.showMessage("Oops!", "Unable to add book to read list, try again later!");
    })
  }

  public removeFromList(listType: string) {
    this.infoSvc.deleteFromQueue(listType, "book", this.bookId).subscribe(data => {
      if(listType == "queue") {
        this.hasQueue = false;
      } else {
        this.hasWatched = false;
      }
      this.showMessage("Success!", "Deleted book successfully!");
    }, error => {
      this.showMessage("Oops!", "Unable to delete book from list, try again later!");
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

  public searchAmazon() : string {
    console.log()
    return `https://www.amazon.com/s?k=${this.bookData["volumeInfo"]["industryIdentifiers"][0]["identifier"]}`
  }
}
