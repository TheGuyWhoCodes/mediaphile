import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewEntityComponent } from './review-entity.component';
import {InfoService} from "../../../../info.service";

describe('ReviewEntityComponent', () => {
  let component: ReviewEntityComponent;
  let fixture: ComponentFixture<ReviewEntityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReviewEntityComponent ],
      providers: [InfoService]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewEntityComponent);
    component = fixture.componentInstance;
    component.review = {
      id: 3,
      timestamp: "1234",
      authorId: "5678",
      authorName: "Chris",
      contentType: "book",
      contentId: "12345",
      contentTitle: "Harry Potter",
      artUrl: "coolimage.com/nice.png",
      reviewTitle: "Nice review",
      reviewBody: "cool book!",
      rating: 3
    }
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
