import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContextStatsComponent } from './context-stats.component';

describe('ContextStatsComponent', () => {
  let component: ContextStatsComponent;
  let fixture: ComponentFixture<ContextStatsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContextStatsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContextStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
