import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-home-entity',
  templateUrl: './home-entity.component.html',
  styleUrls: ['./home-entity.component.scss']
})
export class HomeEntityComponent implements OnInit {

  @Input()
  entity: {} = {}
  type: string

  constructor() { }

  ngOnInit(): void {
    if (this.entity["mediaType"] != undefined){
      this.type = this.entity["mediaType"]
    }
    console.log(this.entity);
    console.log(this.getEntityUrl())
  }
  public getEntityImageUrl() : string {
    if(this.entity["artUrl"]) {
      return this.entity['artUrl']
    }
    return "https://critics.io/img/movies/poster-placeholder.png"
  }

  public getReleaseYear() : string {
    if(this.type == "movie") {
      if(this.entity["release_date"]) {
        return this.entity["release_date"].slice(0,4);
      }
    } else if(this.type == "book") {
      if(this.entity["volumeInfo"]["publishedDate"]) {
        return this.entity["volumeInfo"]["publishedDate"].slice(0,4);
      }
    }
    return "N/A"
  }

  public getEntityUrl() : string {
    if(this.type == "book") {
      return `/book/${this.entity["mediaId"]}`
    } else if(this.type == "movie") {
      return `/movie/${this.entity["mediaId"]}`
    }
  }

  public getEntityTitle() : string {
    if(this.entity["title"]) {
      return this.entity["title"];
    }
  }
}
