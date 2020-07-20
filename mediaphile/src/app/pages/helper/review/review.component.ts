import {Component, Input, OnInit} from '@angular/core';
import {InfoService} from "../../../info.service";
import {LoginStatus} from "../../../auth/login.status";
import {Observable} from "rxjs";
import {Review} from "../../../struct/Review";
import {faEye} from "@fortawesome/free-solid-svg-icons";

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

  faEye = faEye;

  reviews: Observable<Review[]>

  constructor(private infoSvc: InfoService, public loginStatus: LoginStatus) { }

  ngOnInit(): void {
    this.reviews = this.infoSvc.getReviewsForMedia(this.id, this.type)
  }

}
