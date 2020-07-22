import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FollowEntityComponent } from './follow-entity.component';
import {LoginStatus} from "../../../../auth/login.status";
import {HttpClient} from "@angular/common/http";
import {InfoService} from "../../../../info.service";

describe('HomeEntityComponent', () => {
  let component: FollowEntityComponent;
  let fixture: ComponentFixture<FollowEntityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FollowEntityComponent ],
      providers: [InfoService, HttpClient, LoginStatus]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FollowEntityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
