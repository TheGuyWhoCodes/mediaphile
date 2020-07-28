import {Component, Directive, HostListener, Input, OnInit} from '@angular/core';
import {InfoService} from "../../../info.service";
import {ActivatedRoute} from "@angular/router";
import { isEmpty } from 'lodash';
import {faAngleDoubleRight} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-results',
  templateUrl: './results.component.html',
  styleUrls: ['./results.component.scss']
})

export class ResultsComponent implements OnInit {
  @Input()
  public type: string

  public query: string
  public arrayResults: {}[] = [];
  public pageNumber: number;
  public total_results: number;
  public hasResults: boolean = false;
  public canLoadMore: boolean = true;
  public faAngleDoubleRight = faAngleDoubleRight;
  constructor(private route: ActivatedRoute, private infoSvc: InfoService) {

  }

  ngOnInit(): void {
    if(this.route.snapshot.queryParamMap.get("page") == null) {
      this.pageNumber = 1;
    } else {
      this.pageNumber = parseInt(this.route.snapshot.queryParamMap.get("page"));
    }
    this.route.queryParams.subscribe(p =>
      this.search(p.query)
    );
  }

  private search(query: string) {
    this.hasResults = false;
    // reset search entries to empty
    if(this.query != query) {
      this.arrayResults = [];
      this.query = query;
      this.pageNumber = 1;
    }

    if(this.type == "book") {
      this.searchBooks(query);
    } else if(this.type == "movie") {
      this.searchMovies(query);
    }

  }

  private searchMovies(query: string) {
    this.infoSvc.searchMovies(query, this.pageNumber).subscribe(data => {
      this.arrayResults.push.apply(this.arrayResults, data["results"]);
      this.total_results = data["total_results"];
      this.hasResults = true;

      if(data["results"].length !== 20) {
        this.canLoadMore = false;
      }
    });
  }

  private searchBooks(query: string) {
    this.infoSvc.searchBooks(query, this.pageNumber).subscribe(data => {
      this.arrayResults.push.apply(this.arrayResults, data["results"]);
      this.total_results = data["total_results"];
      this.hasResults = true;

      if(data["results"] === null) {
        this.canLoadMore = false;
      }
    });
  }

  public hasReceivedResults() {
    return this.hasResults;
  }

  public loadMore() {
    this.pageNumber++;
    this.search(this.query);
  }

  public searchResultsContainsNone() {
    return this.hasResults && this.arrayResults.length === 0;
  }
}
