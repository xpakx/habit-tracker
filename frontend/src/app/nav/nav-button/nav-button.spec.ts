import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavButton } from './nav-button';

describe('NavButton', () => {
  let component: NavButton;
  let fixture: ComponentFixture<NavButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavButton]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
