import { Component, OnInit } from '@angular/core';
import { faTicketAlt } from '@fortawesome/free-solid-svg-icons';
import {Router} from "@angular/router";
import {LoginStatus} from "../../auth/login.status";

@Component({
  selector: 'app-greeting',
  templateUrl: './greeting.component.html',
  styleUrls: ['./greeting.component.scss']
})
export class GreetingComponent implements OnInit {
  faTicketAlt = faTicketAlt;
  searchInput: string;

  constructor(private router: Router, public loginStatus: LoginStatus) { }

  ngOnInit(): void {
  }

  /**
   * search command will navigate the user to the full search engine with the :query input
   */
  search() {
    this.router.navigate(['/search/'], {queryParams: {query:this.searchInput}});
  }
  keyDownFunction(event) {
    if(event.keyCode == 13) {
      this.search()
    }
  }

}
