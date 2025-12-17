import { Component, ElementRef, input, output, viewChild } from '@angular/core';
import { FontAwesomeModule, IconDefinition } from '@fortawesome/angular-fontawesome';
import { faWarning } from '@fortawesome/free-solid-svg-icons';
import { gsap } from 'gsap';

@Component({
  selector: 'app-nav-button',
  imports: [FontAwesomeModule],
  templateUrl: './nav-button.html',
  styleUrl: './nav-button.css',
})
export class NavButton {
  icon = input<IconDefinition>(faWarning);
  hint = input<string>();

  action = output<boolean>();

  btn = viewChild<ElementRef<HTMLButtonElement>>("btn");
  active: boolean;

  constructor() {
    this.active = false;
  }

  activateNav() {
    this.action.emit(true);
  }

  activate() {
    this.active = true;
    let btn = this.btn();
    if (!btn) return;
    gsap.fromTo(
      btn.nativeElement,
      { scale: 0.8 },
      { scale: 1, duration: 0.5, ease: "elastic.out(1, 0.5)" }
    );
  }

  deactivate() {
    this.active = false;
  }
}
