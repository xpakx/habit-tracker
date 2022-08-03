import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContextsViewComponent } from './contexts-view.component';

describe('ContextsViewComponent', () => {
  let component: ContextsViewComponent;
  let fixture: ComponentFixture<ContextsViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContextsViewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContextsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
