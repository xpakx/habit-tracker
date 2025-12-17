import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavButtonSecondary } from './nav-button-secondary';

describe('NavButtonSecondary', () => {
  let component: NavButtonSecondary;
  let fixture: ComponentFixture<NavButtonSecondary>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavButtonSecondary]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavButtonSecondary);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
