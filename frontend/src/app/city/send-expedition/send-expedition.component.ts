import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EquipmentEntry } from 'src/app/equipment/dto/equipment-entry';
import { EquipmentResponse } from 'src/app/equipment/dto/equipment-response';
import { EquipmentService } from 'src/app/equipment/equipment.service';
import { IslandResponse } from 'src/app/expedition/dto/island-reponse';
import { IslandService } from 'src/app/expedition/island.service';
import { CityService } from '../city.service';
import { DeployedShip } from '../dto/deployed-ship';
import { ExpeditionEquipment } from '../dto/expedition-equipment';
import { ExpeditionRequest } from '../dto/expedition-request';
import { ExpeditionResponse } from '../dto/expedition-response';

@Component({
  selector: 'app-send-expedition',
  templateUrl: './send-expedition.component.html',
  styleUrls: ['./send-expedition.component.css']
})
export class SendExpeditionComponent implements OnInit {
  cityId?: number;
  showShips: boolean = false;
  ships: DeployedShip[] = [];
  shipsToSend: DeployedShip[] = [];
  cargo: Map<number, ExpeditionEquipment[]> = new Map();
  equipment: EquipmentEntry[] = [];
  eqView: number|undefined;
  islandId: number | undefined;
  islands: IslandResponse[] = [];
  islandsLoaded: boolean = false;

  constructor(private cityService: CityService, private eqService: EquipmentService, private route: ActivatedRoute, private islandService: IslandService) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      this.cityId = routeParams['id'];
      if(this.cityId) {
        this.getExpeditions(this.cityId);
        this.getShips(this.cityId);
      } 
    }); 
  }
  
  getExpeditions(cityId: number) {
    throw new Error('Method not implemented.');
  }

  getShips(cityId: number) {
    this.cityService.getShips(cityId).subscribe({
      next: (response: DeployedShip[]) => this.updateShips(response),
      error: (error: HttpErrorResponse) => this.onError(error)
    })
  }

  onError(error: HttpErrorResponse): void {
    throw new Error('Method not implemented.');
  }

  updateShips(response: DeployedShip[]): void {
    this.ships = response;
  }

  addShip(ship: DeployedShip) {
    if(!this.shipsToSend.find(a => a.id == ship.id)) {
      this.shipsToSend.push(ship);
      this.ships = this.ships.filter(a => a.id != ship.id);
    }
  }

  cancelShip(ship: DeployedShip) {
    if(!this.ships.find(a => a.id == ship.id)) {
      this.ships.push(ship);
      this.shipsToSend = this.shipsToSend.filter(a => a.id != ship.id);
    }
  }

  sendExpedition() {
    if(this.cityId) {
      let request: ExpeditionRequest = {islandId: this.islandId, ships: []};
      for(let ship of this.shipsToSend) {
        let cargoForShip: ExpeditionEquipment[] | undefined = this.cargo.get(ship.id);
        request.ships.push({shipId: ship.id, equipment: cargoForShip ? cargoForShip : []});
      }
      this.cityService.sendExpedition(request, this.cityId).subscribe({
        next: (response: ExpeditionResponse) => this.onSuccess(response),
        error: (error: HttpErrorResponse) => this.onError(error)
      });
    }
  }

  addCargoToShip(shipId: number, itemId: number, amount: number) {
    if(!this.cargo.get(shipId)) {
      this.cargo.set(shipId, []);
    }
    this.cargo.get(shipId)?.push({id: itemId, amount: amount});
  }

  unloadCargo(shipId: number) {
    this.cargo.set(shipId, []);
  }

  subtractCargoFromShip(shipId: number, itemId: number) {
    if(!this.cargo.get(shipId)) {
      this.cargo.set(shipId, []);
    }
    let shipCargo = this.cargo.get(shipId);
    this.cargo.set(shipId, shipCargo ? shipCargo.filter(a => a.id == itemId) : []);
  }

  onSuccess(response: ExpeditionResponse): void {
    throw new Error('Method not implemented.');
  }

  switchEquipment(shipId: number): void {
    if(!this.eqView || this.eqView != shipId) {
      this.openEquipment(shipId);
    } else {
      this.closeEquipment();
    }
  }

  openEquipment(shipId: number): void {
    this.eqService.getEquipment().subscribe({
      next: (response: EquipmentResponse) => this.saveEquipment(response, shipId)
    });
  }

  closeEquipment(): void {
    this.eqView = undefined;
  }

  saveEquipment(response: EquipmentResponse, shipId: number): void {
    this.equipment = response.items;
    this.eqView = shipId;
  }

  getIslands() {
    this.islandService.getAllIslands().subscribe({
      next: (response: IslandResponse[]) => this.updateIslands(response),
      error: (error: HttpErrorResponse) => this.onError(error)
    })
  }

  updateIslands(response: IslandResponse[]): void {
    this.islands = response;
    this.islandsLoaded = true;
  }

  chooseIsland(id: number | undefined) {
    this.islandId = id;
  }
}
