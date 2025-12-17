import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavSearch } from './nav-search';

describe('NavSearch', () => {
  let component: NavSearch;
  let fixture: ComponentFixture<NavSearch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavSearch]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavSearch);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
