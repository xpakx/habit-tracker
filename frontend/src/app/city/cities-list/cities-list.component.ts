import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CityService } from '../city.service';
import { City } from '../dto/city';

@Component({
  selector: 'app-cities-list',
  templateUrl: './cities-list.component.html',
  styleUrls: ['./cities-list.component.css']
})
export class CitiesListComponent implements OnInit {
  cities: City[] = [];
  @Output("choice") choiceEvent = new EventEmitter<number>();

  constructor(private cityService: CityService) { }

  ngOnInit(): void {
    this.cityService.getCities().subscribe({
      next: (response: City[]) => this.updateCities(response),
      error: (error: HttpErrorResponse) => this.onError(error)
    });
  }

  onError(error: HttpErrorResponse): void {
    throw new Error('Method not implemented.');
  }

  updateCities(response: City[]): void {
    this.cities = response;
  }

  choose(id: number): void {
    this.choiceEvent.emit(id);
  }
}
