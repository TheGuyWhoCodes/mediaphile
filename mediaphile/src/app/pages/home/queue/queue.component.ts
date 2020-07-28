import {Component, Input, OnInit} from '@angular/core';
import {InfoService} from "../../../info.service";
import {faEye, faAngleDoubleRight} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-queue',
  templateUrl: './queue.component.html',
  styleUrls: ['./queue.component.scss']
})
export class QueueComponent implements OnInit {

  faEye = faEye;

  @Input()
  userID: string;

  @Input()
  viewerId: string;

  @Input()
  type: string;

  public hasResults: boolean = false;

  public entities: [] = []

  public showMore: boolean = true;

  public loaded: boolean = false;

  public pageNumber: number = 1;

  public faAngleDoubleRight = faAngleDoubleRight;

  constructor(private infoSvc: InfoService) { }

  ngOnInit(): void {
    this.getMoreActivity(this.pageNumber)
  }

  public hasReceivedResults() {
    return this.hasResults;
  }

  public isEntitiesEmpty() : boolean {
    return this.entities.length == 0;
  }

  public getEmptyText() : string {
    return (this.userID === this.viewerId) ? "Find something to add!" : "No items added";
  }

  public loadMore() {
    this.pageNumber += 1;
    this.getMoreActivity(this.pageNumber);
  }

  public getMoreActivity(offset: number) {
    this.loaded = false;
      this.infoSvc.getQueue(this.userID, this.type, offset).subscribe(data => {
        this.hasResults = true;
        this.entities.push.apply(this.entities, data)
        if(data.length != 24) {
          this.showMore = false;
        }
        this.loaded = true;
      })
    }
}
