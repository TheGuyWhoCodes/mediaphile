import {Component, Input, OnInit} from '@angular/core';
import {InfoService} from "../../../../info.service";

@Component({
  selector: 'app-follow-entity',
  templateUrl: './follow-entity.component.html',
  styleUrls: ['./follow-entity.component.scss']
})
export class FollowEntityComponent implements OnInit {

  @Input()
  userId: string;

  public entity: {};
  public hasResults: boolean;

  constructor(private infoSvc: InfoService) { }

  ngOnInit(): void {
    this.infoSvc.getUser(this.userId).subscribe(data => {
      this.entity = data;
      this.hasResults = true;
    });
  }
}
