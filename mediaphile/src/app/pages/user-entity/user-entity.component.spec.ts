import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserEntityComponent } from './user-entity.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {InfoService} from "../../info.service";
import {HttpClient, HttpHandler} from "@angular/common/http";
import {LoginStatus} from "../../auth/login.status";

describe('UserEntityComponent', () => {
  let component: UserEntityComponent;
  let fixture: ComponentFixture<UserEntityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserEntityComponent ],
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [InfoService, HttpClient, LoginStatus]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserEntityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
