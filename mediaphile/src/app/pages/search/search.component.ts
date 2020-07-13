import { Component, OnInit } from '@angular/core';
import {Title} from "@angular/platform-browser";
import { faFilm, faBook } from '@fortawesome/free-solid-svg-icons';
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
  public book = "book"
  public movie = "movie"

  constructor(private title: Title, private route: ActivatedRoute) {

  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(p =>
      this.title.setTitle(`Mediaphile Search Results for "` + p.query + `"`)
    );
  }

}
