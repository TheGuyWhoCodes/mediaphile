import {Component, Input, OnInit} from '@angular/core';
import {InfoService} from "../../../info.service";
import {faEye} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-queue',
  templateUrl: './queue.component.html',
  styleUrls: ['./queue.component.scss']
})
export class QueueComponent implements OnInit {

  faEye = faEye;

  @Input()
  userID: string

  @Input()
  type: string
  public hasResults: boolean = false;
  public entities: [] = [];
  constructor(private infoSvc: InfoService) { }

  ngOnInit(): void {
    this.infoSvc.getQueue(this.userID, this.type).subscribe(x => {
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

}
