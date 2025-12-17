import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavUser } from './nav-user';

describe('NavUser', () => {
  let component: NavUser;
  let fixture: ComponentFixture<NavUser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavUser]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavUser);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
