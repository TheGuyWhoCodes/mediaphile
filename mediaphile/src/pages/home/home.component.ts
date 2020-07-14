import { Component, OnInit } from '@angular/core';
import {LoginStatus} from "../../auth/login.status";
import {Title} from "@angular/platform-browser";
import {faClipboardCheck, faClock, faUserFriends} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  clip = faClock
  clipCheck = faClipboardCheck
  friends = faUserFriends

  constructor(public loginStatus: LoginStatus, private title: Title) { }

  ngOnInit(): void {
    this.title.setTitle(`Mediaphile :: Home`)
  }

}
