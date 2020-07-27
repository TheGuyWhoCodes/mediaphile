import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityEntityComponent } from './activity-entity.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {InfoService} from "../../../../info.service";
import {HttpClient} from "@angular/common/http";
import {LoginStatus} from "../../../../auth/login.status";

describe('ActivityEntityComponent', () => {
  let component: ActivityEntityComponent;
  let fixture: ComponentFixture<ActivityEntityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ActivityEntityComponent ],
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [InfoService, HttpClient, LoginStatus]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActivityEntityComponent);
    component = fixture.componentInstance;
    component.activity = {
      "artUrl": "coolUrl.com"
    }
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
