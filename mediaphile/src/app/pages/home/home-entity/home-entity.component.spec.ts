import { async, ComponentFixture, TestBed } from '@angular/core/testing';


import { HomeEntityComponent } from './home-entity.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {InfoService} from "../../../info.service";
import {HttpClient, HttpHandler} from "@angular/common/http";
import {LoginStatus} from "../../../auth/login.status";

describe('HomeEntityComponent', () => {
  let component: HomeEntityComponent;
  let fixture: ComponentFixture<HomeEntityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HomeEntityComponent ],
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [InfoService, HttpClient, LoginStatus]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeEntityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
