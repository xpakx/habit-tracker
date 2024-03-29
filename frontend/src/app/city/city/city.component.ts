import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EquipmentEntry } from 'src/app/equipment/dto/equipment-entry';
import { EquipmentResponse } from 'src/app/equipment/dto/equipment-response';
import { EquipmentService } from 'src/app/equipment/equipment.service';
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
  cityId?: number;
  tab:number = 0;

  constructor(private cityService: CityService, private eqService: EquipmentService, private route: ActivatedRoute) { }

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
    if(this.tab == 1) {
      this.tab = 0;
      return;
    }
    this.tab = 1;
    this.eqService.getBuildings().subscribe({
      next: (response: EquipmentResponse) => this.updatePlans(response),
      error: (error: HttpErrorResponse) => this.onError(error)
    });
  }

  updatePlans(response: EquipmentResponse): void {
    this.plans = response.items;
  }

  onBuildingClick(plan: EquipmentEntry): void {
    this.build(plan.itemId);
  }

  onShipClick(ship: EquipmentEntry): void {
    if(this.cityId) {
      this.cityService.deploy({entryId: ship.id}, this.cityId).subscribe({
        next: (response: ShipResponse) => this.addShip(response, ship.id),
        error: (error: HttpErrorResponse) => this.onError(error)
      });
    }
  }
  
  addShip(response: ShipResponse, entryId: number): void {
    this.shipsToDeploy = this.shipsToDeploy.filter(a => a.id != entryId);
    throw new Error('Method not implemented.');
  }

  switchShipContainer(): void {
    if(this.tab == 2) {
      this.tab = 0;
      return;
    }
    this.tab = 2;
    this.eqService.getShips().subscribe({
      next: (response: EquipmentResponse) => this.updateShips(response),
      error: (error: HttpErrorResponse) => this.onError(error)
    });
  }

  updateShips(response: EquipmentResponse): void {
    this.shipsToDeploy = response.items;
  }
}
