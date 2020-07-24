import {Component, Input, OnInit} from '@angular/core';
import {InfoService} from "../../../info.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Observable, Subscribable} from "rxjs";
import {LoginStatus} from "../../../auth/login.status";

@Component({
  selector: 'app-review-submit',
  templateUrl: './review-submit.component.html',
  styleUrls: ['./review-submit.component.scss']
})
export class ReviewSubmitComponent implements OnInit {

  @Input()
  id: string

  @Input()
  type: string

  @Input()
  title: string

  @Input()
  artUrl: string

  successfullySubmitted: boolean = false;

  public error: any

  public loading: boolean = false;

  hero = { title: '', review: '', currentRate: 0 };

  currentUser: {};

  constructor(private infoSvc: InfoService, public loginStatus: LoginStatus) { }

  ngOnInit(): void {
    this.loginStatus.sharedAccountId.subscribe(userId => {
      this.infoSvc.getUser(userId).subscribe(userData => {
        this.currentUser = userData;
      });
    });
  }

  public submitReview() : void {
    if (this.currentUser == undefined) {
      this.error = "Not logged in";
      return;
    }
    this.loading = true;
    this.infoSvc.postReviewForMedia(this.currentUser['id'], this.currentUser['username'],
      this.type, this.id, this.title, this.artUrl, this.hero.title, this.hero.review,  this.hero.currentRate).subscribe(reviewStatus => {
      this.successfullySubmitted = true;
      this.error = undefined;
      this.loading = false;
    },error => {
        this.error = error;
        this.loading = false;
    })
  }

  reload() {
    location.reload();
  }
}
