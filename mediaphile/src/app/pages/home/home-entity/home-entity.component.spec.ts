import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeEntityComponent } from './home-entity.component';

describe('HomeEntityComponent', () => {
  let component: HomeEntityComponent;
  let fixture: ComponentFixture<HomeEntityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HomeEntityComponent ]
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
