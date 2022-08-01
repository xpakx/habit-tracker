import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContextModalComponent } from './context-modal.component';

describe('ContextModalComponent', () => {
  let component: ContextModalComponent;
  let fixture: ComponentFixture<ContextModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContextModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContextModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
