import { Component, OnInit } from '@angular/core';
import {InfoService} from "../../../info.service";
import {Observable} from "rxjs";
import {LoginStatus} from "../../../auth/login.status";
import {ModalComponent} from "../../helper/modal/modal.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import { faEye } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-activity',
  templateUrl: './activity.component.html',
  styleUrls: ['./activity.component.scss']
})
export class ActivityComponent implements OnInit {

  public activity: {}[] = [];
  faEye = faEye;
  public loaded: boolean = false;
  public showMore: boolean = true;
  public pageNumber: number = 0;
  constructor(private infoSvc: InfoService, private loginStatus: LoginStatus, private modalService: NgbModal) { }

  ngOnInit(): void {
    this.getMoreActivity(0);
  }

  public getMoreActivity(pageNumber: number) {
    this.loaded = false;
    this.loginStatus.sharedAccountId.subscribe(id => {
      this.infoSvc.getActivity(id, pageNumber).subscribe(data => {
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

  public isEntitiesEmpty() {
    return this.activity.length == 0;
  }
}

