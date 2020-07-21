import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FollowListComponent } from './follow-list.component';
import {InfoService} from "../../../info.service";
import {HttpClient, HttpHandler} from "@angular/common/http";
import {Router} from "@angular/router";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LoginStatus} from "../../../auth/login.status";

describe('QueueComponent', () => {
  let component: FollowListComponent;
  let fixture: ComponentFixture<FollowListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FollowListComponent ],
      imports: [ HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [InfoService, LoginStatus]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FollowListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
