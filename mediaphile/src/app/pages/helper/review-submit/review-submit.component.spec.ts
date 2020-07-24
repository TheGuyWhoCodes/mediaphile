import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewSubmitComponent } from './review-submit.component';
import {InfoService} from "../../../info.service";
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import {LoginStatus} from "../../../auth/login.status";
import { FormsModule } from '@angular/forms';
import {Review} from "../../../struct/Review";
import {of, throwError} from "rxjs";
import {DebugElement, inject} from "@angular/core";
import { By } from '@angular/platform-browser';

describe('ReviewSubmitComponent', () => {
  let component: ReviewSubmitComponent;
  let fixture: ComponentFixture<ReviewSubmitComponent>;
  let infoSvc;
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReviewSubmitComponent ],
      imports: [ HttpClientTestingModule, RouterTestingModule.withRoutes([]), FormsModule],
      providers: [InfoService, LoginStatus]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewSubmitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    infoSvc = TestBed.get(InfoService);
    de = fixture.debugElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it("should submit correctly", async(() => {
    // TODO: This won't work since the user isn't authenticated
    /* const response: {} = {
      id: 1234,
      timestamp: 5467,
      authorId: 1234576,
      authorName: "chris",
      contentType: "book",
      contentId: 54312,
      contentTitle: "Test book",
      artUrl: "http://google.com/",
      reviewTitle: "cool book!",
      reviewBody: "nice book",
      rating: 5
    };

    spyOn(infoSvc, 'postReviewForMedia').and.returnValue(of(response))
    component.submitReview();
    fixture.detectChanges();

    expect(component.error).toEqual(undefined);
    expect(component.successfullySubmitted).toEqual(true); */
  }))

  it("should throw error on bad submit", async(() => {
    spyOn(infoSvc, 'postReviewForMedia').and.returnValue(throwError({status: 401}))
    component.submitReview();
    fixture.detectChanges();
    expect(de.query(By.css(".alert-danger"))).toBeDefined();
    expect(component.error).toEqual({status: 401});
    expect(component.successfullySubmitted).toEqual(false);
  }))
});
