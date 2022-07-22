import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HabitListComponent } from './habit-list.component';

describe('HabitListComponent', () => {
  let component: HabitListComponent;
  let fixture: ComponentFixture<HabitListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HabitListComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HabitListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
