import { afterNextRender, Component } from '@angular/core';
import { NavPill } from '../nav-pill/nav-pill';
import gsap from 'gsap';
import { NavBrand } from '../nav-brand/nav-brand';
import { NavUser } from '../nav-user/nav-user';
import { NavButtonSecondary } from '../nav-button-secondary/nav-button-secondary';
import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { faBell } from '@fortawesome/free-regular-svg-icons';

@Component({
  selector: 'app-topbar',
  imports: [NavBrand, NavPill, NavButtonSecondary, NavUser],
  templateUrl: './topbar.html',
  styleUrl: './topbar.css',
})
export class Topbar {
  add = faPlus;
  notif = faBell;

  constructor() {
    afterNextRender(this.enterView);
  }

  enterView() {
    gsap.from(
      ".topbar",
      { y: -72, opacity: 0, duration: 1, ease: "power3.out" }
    );
  }
}
