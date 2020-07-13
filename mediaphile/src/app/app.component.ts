import {Component, OnInit} from '@angular/core';
import {InfoService} from "./info.service";
import {LoginStatus} from "./auth/login.status";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'mediaphile';

  constructor(private infoSvc: InfoService, private loginStatus: LoginStatus) {
  }
  ngOnInit(): void {
    this.infoSvc.login().toPromise().then(x => {
      this.loginStatus.setValues(x.id, x.url, x.loggedIn)
    })
  }
}
