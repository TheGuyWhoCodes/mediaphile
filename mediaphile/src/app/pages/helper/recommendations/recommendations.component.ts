import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {InfoService} from "../../../info.service";
import {faChevronLeft, faChevronRight} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-recommendations',
  templateUrl: './recommendations.component.html',
  styleUrls: ['./recommendations.component.scss']
})
export class RecommendationsComponent implements OnInit {

  @Input()
  mediaType: string

  @Input()
  mediaId: string

  slides: [] = []

  error: boolean = false;

  faRightChev = faChevronRight
  faLeftChev = faChevronLeft


  // used to scroll sideways
  @ViewChild('recSlides') recSlides: ElementRef;

  private SCROLL_DISTANCE: number = 300;

  constructor(private infoSvc: InfoService) { }

  ngOnInit(): void {
    this.infoSvc.getRecommendations(this.mediaId, this.mediaType).subscribe(data => {
      this.slides = data["results"]
    }, error => {
      this.error = true;
    })
  }

  scrollLeft(){
    this.recSlides.nativeElement.scrollLeft -= this.SCROLL_DISTANCE;
  }

  scrollRight(){
    this.recSlides.nativeElement.scrollLeft += this.SCROLL_DISTANCE;
  }

  isRecEmpty() {
    return this.slides == null || this.slides.length == 0 || this.error;
  }
}
