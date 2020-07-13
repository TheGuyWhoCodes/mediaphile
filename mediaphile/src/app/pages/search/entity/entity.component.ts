import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-entity',
  templateUrl: './entity.component.html',
  styleUrls: ['./entity.component.scss']
})
export class EntityComponent implements OnInit {

  @Input()
  entity: {}

  @Input()
  type: string

  constructor() { }

  ngOnInit(): void {

  }

  public getEntityImageUrl() : string {
    if(this.type == "book") {
      if("imageLinks" in this.entity["volumeInfo"]) {
        return this.entity["volumeInfo"]["imageLinks"]["thumbnail"]
      }
    } else if(this.type == "movie") {
      if(this.entity["poster_path"]) {
        return "https://image.tmdb.org/t/p/w500" + this.entity['poster_path']
      }
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
      return `/book/${this.entity["id"]}`
    } else if(this.type == "movie") {
      return `/movie/${this.entity["id"]}`
    }
  }

  public getEntityTitle() : string {
    if(this.type == "movie") {
      return this.entity["title"];
    } else if(this.type == "book") {
      return this.entity["volumeInfo"]["title"];
    } else {
      return "N/A"
    }
  }
}



