import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavPill } from './nav-pill';

describe('NavPill', () => {
  let component: NavPill;
  let fixture: ComponentFixture<NavPill>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavPill]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavPill);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
