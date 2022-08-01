import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DailyContextViewComponent } from './daily-context-view.component';

describe('DailyContextViewComponent', () => {
  let component: DailyContextViewComponent;
  let fixture: ComponentFixture<DailyContextViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DailyContextViewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DailyContextViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
