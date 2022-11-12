import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EquipmentEntry } from 'src/app/equipment/dto/equipment-entry';
import { CityService } from '../city.service';
import { Building } from '../dto/building';
import { BuildingResponse } from '../dto/building-response';
import { ShipResponse } from '../dto/ship-response';

@Component({
  selector: 'app-city',
  templateUrl: './city.component.html',
  styleUrls: ['./city.component.css']
})
export class CityComponent implements OnInit {
  buildings: Building[] = [];
  plans: EquipmentEntry[] = [];
  shipsToDeploy: EquipmentEntry[] = [];
  showPlans: boolean = false;
  showShips: boolean = false;
  cityId?: number;

  constructor(private cityService: CityService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      this.cityId = routeParams['id'];
      if(this.cityId) {
        this.getCity(this.cityId);
      } 
    }); 
  }

  getCity(cityId: number): void {
    this.cityService.getBuildings(cityId).subscribe({
      next: (response: Building[]) => this.updateBuildings(response),
      error: (error: HttpErrorResponse) => this.onError(error)
    });
  }

  onError(error: HttpErrorResponse): void {
    throw new Error('Method not implemented.');
  }

  updateBuildings(response: Building[]): void {
    this.buildings = response;
  }

  build(buildingId: number) {
    if(this.cityId) {
      this.cityService.build({buildingId: buildingId}, this.cityId).subscribe({
        next: (response: BuildingResponse) => this.addBuilding(response),
        error: (error: HttpErrorResponse) => this.onError(error)
      });
    }
  }

  addBuilding(response: BuildingResponse): void {
    throw new Error('Method not implemented.');
  }

  switchBuildContainer(): void {
    this.showPlans = !this.showPlans;
  }

  onBuildingClick(plan: EquipmentEntry): void {
    this.build(plan.itemId);
  }

  onShipClick(ship: EquipmentEntry): void {
    if(this.cityId) {
      this.cityService.deploy({entryId: ship.id}, this.cityId).subscribe({
        next: (response: ShipResponse) => this.addShip(response),
        error: (error: HttpErrorResponse) => this.onError(error)
      });
    }
  }
  
  addShip(response: ShipResponse): void {
    throw new Error('Method not implemented.');
  }

  switchShipContainer(): void {
    this.showShips = !this.showShips;
  }
}
