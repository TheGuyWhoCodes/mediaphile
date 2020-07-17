import {Component, Input, OnInit} from '@angular/core';
import {InfoService} from "../../../info.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Observable, Subscribable} from "rxjs";

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

  successfullySubmitted: boolean = false;

  public error: any

  public loading: boolean = false;

  hero = { title: '', review: '', currentRate: 0 };

  constructor(private infoSvc: InfoService) { }

  ngOnInit(): void {

  }

  public submitReview() : void {
    this.loading = true;
    this.infoSvc.postReviewForMedia(this.id, this.type, this.hero.currentRate, this.hero.title, this.hero.review).subscribe(reviewStatus => {
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
