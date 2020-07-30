import {Component, Input, OnInit} from '@angular/core';
import {InfoService} from "../../../info.service";
import {LoginStatus} from "../../../auth/login.status";
import {Observable} from "rxjs";
import {Review} from "../../../struct/Review";
import {faEye} from "@fortawesome/free-solid-svg-icons";
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

  reviews: Observable<Review[]>
  selfReview: Review;
  userHasReview: boolean = false;

  constructor(private infoSvc: InfoService, private router: Router, public loginStatus: LoginStatus) { }

  ngOnInit(): void {
    this.reviews = this.infoSvc.getReviewsForMedia(this.id, this.type);
    this.loginStatus.sharedAccountId.subscribe(userId => {
      this.infoSvc.getSpecificReview(this.id, this.type, userId).subscribe(review => {
        if (review != undefined) {
          this.selfReview = review;
          this.userHasReview = true;
        }
      });
    });

  }

  loginWithRedirect() {
    this.router.navigate(["/login"], {queryParams: {redirect: this.router.url}});
  }

  matchesSelfReview(reviewId: number): boolean {
    return (this.selfReview != undefined) && (reviewId == this.selfReview.id);
  }

}
