import { afterNextRender, Component } from '@angular/core';
import { NavPill } from '../nav-pill/nav-pill';
import gsap from 'gsap';
import { NavBrand } from '../nav-brand/nav-brand';
import { NavUser } from '../nav-user/nav-user';

@Component({
  selector: 'app-topbar',
  imports: [NavBrand, NavPill, NavUser],
  templateUrl: './topbar.html',
  styleUrl: './topbar.css',
})
export class Topbar {

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
