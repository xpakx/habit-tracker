import { Component } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faSearch } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-nav-search',
  imports: [FontAwesomeModule],
  templateUrl: './nav-search.html',
  styleUrl: './nav-search.css',
})
export class NavSearch {
  searchIcon = faSearch;

}
