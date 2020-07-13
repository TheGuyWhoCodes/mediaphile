import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Router, NavigationStart } from '@angular/router';
import { filter } from 'rxjs/operators';
import {faTicketAlt, faUserCircle} from '@fortawesome/free-solid-svg-icons';
import {InfoService} from "../info.service";
import {LoginStatus} from "../auth/login.status";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  faTicketAlt = faTicketAlt;
  faPersonBooth = faUserCircle
  searchInput: string;

  @Output() navToggled = new EventEmitter();
  navOpen = false;

  constructor(private router: Router, private infoSvc: InfoService, public loginStatus: LoginStatus) {
  }

  ngOnInit() {
    // If nav is open after routing, close it
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationStart && this.navOpen)
      )
      .subscribe(event => this.toggleNav());
  }

  toggleNav() {
    this.navOpen = !this.navOpen;
    this.navToggled.emit(this.navOpen);
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

  logout() {
    this.infoSvc.logout();
  }
}
