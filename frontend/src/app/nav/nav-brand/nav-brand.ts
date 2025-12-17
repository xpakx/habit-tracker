import { Component } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faAnchor } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-nav-brand',
  imports: [FontAwesomeModule],
  templateUrl: './nav-brand.html',
  styleUrl: './nav-brand.css',
})
export class NavBrand {
  logo = faAnchor;
}
