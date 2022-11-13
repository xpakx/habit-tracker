import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SendExpeditionComponent } from './send-expedition.component';

describe('SendExpeditionComponent', () => {
  let component: SendExpeditionComponent;
  let fixture: ComponentFixture<SendExpeditionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SendExpeditionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SendExpeditionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
