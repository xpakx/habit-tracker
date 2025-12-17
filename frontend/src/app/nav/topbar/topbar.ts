import { afterNextRender, Component } from '@angular/core';
import { NavPill } from '../nav-pill/nav-pill';
import gsap from 'gsap';

@Component({
  selector: 'app-topbar',
  imports: [NavPill],
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
