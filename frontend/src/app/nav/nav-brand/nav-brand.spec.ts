import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavBrand } from './nav-brand';

describe('NavBrand', () => {
  let component: NavBrand;
  let fixture: ComponentFixture<NavBrand>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavBrand]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavBrand);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
