import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-activity-entity',
  templateUrl: './activity-entity.component.html',
  styleUrls: ['./activity-entity.component.scss']
})
export class ActivityEntityComponent implements OnInit {

  @Input()

  activity: Object

  constructor() { }

  ngOnInit(): void {

  }

  public isReview(): boolean {
    return "reviewTitle" in this.activity
  }

  public getArtwork() {
    if(this.isReview() && (this.activity["contentType"] === "movie")) {
        return "https://image.tmdb.org/t/p/w500" + this.activity["artUrl"]
    }
    return this.activity["artUrl"];
  }

  public getTitle(): string {
    if(this.isReview()) {
      return this.activity["contentTitle"]
    }
    return this.activity["title"]
  }

  public getAuthor(): string {
    if(this.isReview()) {
      return this.activity["authorName"];
    }
    return "Someone you follow "
  }

  public getRating() : string {
    if(this.isReview()) {
      return this.activity["rating"]
    }
    return ""
  }

  public getListType() : string {
    if(!this.isReview()) {
      return this.activity["listType"]
    }
    return ""
  }

  public getActivityMessage(): string {
    if(this.isReview()) {
      return `${this.getAuthor()} rated "${this.getTitle()}" ${this.getRating()} stars!`
    }
    return `${this.getAuthor()} added ${this.getTitle()} to their "${this.getListType()}" list`
  }

  public getActivityUrl(): string {
    if(this.isReview()) {
      return `${this.activity["contentType"]}/${this.activity["contentId"]}`
    }
    return `${this.activity["mediaType"]}/${this.activity["mediaId"]}`
  }

  public getAuthorUrl(): string {
    if(this.isReview()) {
      return "/user/" + this.activity["authorId"]
    }
    return "/user/" + this.activity["userId"]
  }
}
