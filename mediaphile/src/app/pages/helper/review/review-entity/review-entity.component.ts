import {Component, Input, OnInit} from '@angular/core';
import {Review} from "../../../../struct/Review";

@Component({
  selector: 'app-review-entity',
  templateUrl: './review-entity.component.html',
  styleUrls: ['./review-entity.component.scss']
})
export class ReviewEntityComponent implements OnInit {

  @Input()
  review: Review
  currentRate:number = 0;

  show: boolean = false;
  constructor() { }

  ngOnInit(): void {
    this.currentRate = this.review.rating
  }

}
