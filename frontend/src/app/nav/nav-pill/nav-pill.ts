import { Component, viewChildren } from '@angular/core';
import { NavButton } from '../nav-button/nav-button';
import { faAnchor, faChartPie, faCog, faHome } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-nav-pill',
  imports: [NavButton],
  templateUrl: './nav-pill.html',
  styleUrl: './nav-pill.css',
})
export class NavPill {
  home = faHome;
  game = faAnchor;
  chart = faChartPie;
  settings = faCog;

  buttons = viewChildren(NavButton);

  constructor() {
  }

  navAction(id: number) {
    console.log(id);
    let i: number = 0;
    for (let btn of this.buttons()) {
      if (i == id) btn.activate();
      else btn.deactivate();
      i++;
    }
  }

}
