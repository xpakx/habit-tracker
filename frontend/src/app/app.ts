import { Component, signal, viewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavButton } from './nav/nav-button/nav-button';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavButton],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');

  btn = viewChild(NavButton);

  constructor() {
  }

  button() {
    const btn = this.btn();
    if (!btn) return;
    if (btn.active) btn.deactivate();
    else btn.activate();
  }

}
