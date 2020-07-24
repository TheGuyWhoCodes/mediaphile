import {Component, Input, OnInit} from '@angular/core';
import {InfoService} from "../../../../info.service";

@Component({
  selector: 'app-follow-entity',
  templateUrl: './follow-entity.component.html',
  styleUrls: ['./follow-entity.component.scss']
})
export class FollowEntityComponent implements OnInit {

  @Input()
  public userId: string;

  @Input()
  public entity: {};
  public hasResults: boolean;

  constructor(private infoSvc: InfoService) { }

  ngOnInit(): void {
    this.hasResults = this.entity != {} && this.entity != undefined;
    if (this.entity != undefined) {
      if (this.entity['profilePicUrl'] === "") {
        this.entity['profilePicUrl'] =
          "https://3.bp.blogspot.com/-qDc5kIFIhb8/UoJEpGN9DmI/AAAAAAABl1s/BfP6FcBY1R8/s320/BlueHead.jpg";
      }
    }
  }
}
