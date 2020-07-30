import {Component, Input, OnInit} from '@angular/core';
import {InfoService} from "../../../info.service";
import {LoginStatus} from "../../../auth/login.status";
import {Observable} from "rxjs";
import {Review} from "../../../struct/Review";
import {faEye, faAngleDoubleRight} from "@fortawesome/free-solid-svg-icons";
import {Router} from "@angular/router";

@Component({
  selector: 'app-review',
  templateUrl: './review.component.html',
  styleUrls: ['./review.component.scss']
})
export class ReviewComponent implements OnInit {

  @Input()
  id: string

  @Input()
  type: string

  @Input()
  title: string

  @Input()
  artUrl: string

  faEye = faEye;

  public faAngleDoubleRight = faAngleDoubleRight;

  pageNumber: number = 1;

  reviews: Review[] = []

  hasReviews: boolean = false;

  showMore: boolean = true;

  loaded: boolean = false;

  constructor(private infoSvc: InfoService, private router: Router, public loginStatus: LoginStatus) { }

  ngOnInit(): void {
    this.getMoreActivity(this.pageNumber)
  }

  loginWithRedirect() {
    this.router.navigate(["/login"], {queryParams: {redirect: this.router.url}});
  }

  public loadMore() {
    this.pageNumber += 1;
    this.getMoreActivity(this.pageNumber);
  }

  public hasReceivedResults() {
    return this.hasReviews;
  }

  public isEntitiesEmpty() : boolean {
    return this.loaded && this.reviews.length == 0;
  }

  public shouldShowMore() : boolean {
    return this.showMore && this.loaded;
  }

  public getMoreActivity(pageNumber: number) {
    this.loaded = false;
    this.infoSvc.getReviewsForMedia(this.id, this.type, pageNumber).subscribe(data => {
      this.reviews.push.apply(this.reviews, data)
      if(this.reviews.length !== 0) {
        this.hasReviews = true;
      }

      if(data.length !== 2) {
        this.showMore = false;
      }
      this.loaded = true;
    });
  }
}
