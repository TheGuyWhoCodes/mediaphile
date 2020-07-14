import { Component, OnInit } from '@angular/core';
import {InfoService} from "../../info.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  constructor(private infoSvc: InfoService) { }

  ngOnInit(): void {
  }

  public login() {
    this.infoSvc.login().subscribe(loginStatus => {
      window.location.href = loginStatus["url"];
    })
  }
}
