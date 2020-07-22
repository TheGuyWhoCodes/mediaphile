import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FollowEntityComponent } from './follow-entity.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {InfoService} from "../../../../info.service";
import {HttpClient, HttpHandler} from "@angular/common/http";
import {LoginStatus} from "../../../../auth/login.status";

describe('FollowEntityComponent', () => {
  let component: FollowEntityComponent;
  let fixture: ComponentFixture<FollowEntityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FollowEntityComponent ],
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
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
