import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityEntityComponent } from './activity-entity.component';

describe('ActivityEntityComponent', () => {
  let component: ActivityEntityComponent;
  let fixture: ComponentFixture<ActivityEntityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ActivityEntityComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActivityEntityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
