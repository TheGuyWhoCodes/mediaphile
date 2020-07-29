import {Component, Input, OnInit} from '@angular/core';
import {InfoService} from "../../info.service";

@Component({
  selector: 'app-user-entity',
  templateUrl: './user-entity.component.html',
  styleUrls: ['./user-entity.component.scss']
})
export class UserEntityComponent implements OnInit {

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
        this.entity['profilePicUrl'] = "assets/blue-head.png";
      }
    }
  }
}
