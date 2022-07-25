import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HabitModalComponent } from './habit-modal.component';

describe('HabitModalComponent', () => {
  let component: HabitModalComponent;
  let fixture: ComponentFixture<HabitModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HabitModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HabitModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
