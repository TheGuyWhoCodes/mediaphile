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
  userID: string;

  @Input()
  viewerId: string;

  @Input()
  type: string;
  public hasResults: boolean = false;
  public entities: [] = [];
  constructor(private infoSvc: InfoService) { }

  ngOnInit(): void {
    this.infoSvc.getFollowList(this.userID, this.type).subscribe(x => {
      this.entities.push.apply(this.entities, x)
      this.hasResults = true;
    })
  }

  public hasReceivedResults() {
    return this.hasResults;
  }

  public isEntitiesEmpty() : boolean {
    return this.entities.length == 0;
  }

  public getEmptyText() : string {
    if (this.type == "followers") {
      return (this.userID === this.viewerId) ? "Find someone to follow!" : "No followers";
    } else if (this.type == "following") {
      return "Not following anyone";
    }
    return "";
  }

}
