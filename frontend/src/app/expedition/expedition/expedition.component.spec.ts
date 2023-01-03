import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpeditionComponent } from './expedition.component';

describe('ExpeditionComponent', () => {
  let component: ExpeditionComponent;
  let fixture: ComponentFixture<ExpeditionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExpeditionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpeditionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
