import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HabitComponent } from './habit.component';

describe('HabitComponent', () => {
  let component: HabitComponent;
  let fixture: ComponentFixture<HabitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HabitComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HabitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
