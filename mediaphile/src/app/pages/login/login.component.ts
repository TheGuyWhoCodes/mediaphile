import { Component, OnInit } from '@angular/core';
import {InfoService} from "../../info.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  private redirect: string;

  constructor(private infoSvc: InfoService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['redirect']) this.redirect = params['redirect'];
    });
  }

  public login() {
    let redirect_query = (this.redirect) ? ("?redirect=" + this.redirect) : "";
    this.infoSvc.login().subscribe(loginStatus => {
      window.location.href = loginStatus["url"] + redirect_query;
    })
  }
}
