import { Component, OnInit } from '@angular/core';
import {Title} from "@angular/platform-browser";
import {faFilm, faBook, faUsers} from '@fortawesome/free-solid-svg-icons';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

  public query: string
  public faFilm = faFilm;
  public faBook = faBook
  public faUsers = faUsers
  public book = "book"
  public movie = "movie"
  public user = "user"

  public entities: [] = [];

  constructor(private title: Title, private route: ActivatedRoute) {

  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(p =>
      this.title.setTitle(`Mediaphile Search Results for "` + p.query + `"`)
    );
  }

}
