import {Component, Input, OnInit} from '@angular/core';
import {InfoService} from "../../../info.service";
import {faEye} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-follow-list',
  templateUrl: './follow-list.component.html',
  styleUrls: ['./follow-list.component.scss']
})
export class FollowListComponent implements OnInit {

  faEye = faEye;

  @Input()
  userId: string;

  @Input()
  viewerId: string;

  @Input()
  type: string;

  @Input()
  public entities: [] = [];

  constructor(private infoSvc: InfoService) { }

  ngOnInit(): void { }

  public hasResults() {
    return this.entities && this.entities.length > 0;
  }

  public getEmptyText() : string {
    if (this.type == "followers") {
      return (this.userId === this.viewerId) ? "Find someone to follow!" : "No followers";
    } else if (this.type == "following") {
      return "Not following anyone";
    }
    return "";
  }

}
