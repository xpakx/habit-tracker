import { Component, input, output } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faWarning, IconDefinition } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-nav-button-secondary',
  imports: [FontAwesomeModule],
  templateUrl: './nav-button-secondary.html',
  styleUrl: './nav-button-secondary.css',
})
export class NavButtonSecondary {
  active: boolean;
  icon = input<IconDefinition>(faWarning);
  hint = input<string>();
  action = output<boolean>();
  badge = input<boolean>(false);

  constructor() {
    this.active = false;
  }

  activateNav() {
    this.action.emit(true);
  }

}
