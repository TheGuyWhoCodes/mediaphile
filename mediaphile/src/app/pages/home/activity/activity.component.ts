import { Component, OnInit } from '@angular/core';
import {InfoService} from "../../../info.service";
import {Observable} from "rxjs";
import {LoginStatus} from "../../../auth/login.status";
import {ModalComponent} from "../../helper/modal/modal.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-activity',
  templateUrl: './activity.component.html',
  styleUrls: ['./activity.component.scss']
})
export class ActivityComponent implements OnInit {

  public activity: {}[] = [];
  public loaded: boolean = false;
  public showMore: boolean = true;
  public pageNumber: number = 0;
  constructor(private infoSvc: InfoService, private loginStatus: LoginStatus, private modalService: NgbModal) { }

  ngOnInit(): void {
    this.getMoreActivity(0);
  }

  public getMoreActivity(offset: number) {
    this.loaded = false;
    this.loginStatus.sharedAccountId.subscribe(id => {
      this.infoSvc.getActivity(id, offset).subscribe(data => {
        this.activity.push.apply(this.activity, data)
        if(data.length != 10) {
          this.showMore = false;
        }
        this.loaded = true;
      })
    }, error => {
      this.loaded = true;
      this.showMessage("Oops", "Couldn't load activity feed!")
    })
  }

  public showMessage(title: string, message: string) {
    const modalRef = this.modalService.open(ModalComponent);
    modalRef.componentInstance.title = title
    modalRef.componentInstance.message = message
  }

  public loadMore() {
    this.pageNumber += 10;
    this.getMoreActivity(this.pageNumber);
  }
}

