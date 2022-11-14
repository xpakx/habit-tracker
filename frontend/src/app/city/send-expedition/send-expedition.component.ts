import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
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

  constructor(private cityService: CityService, private route: ActivatedRoute) { }

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
      let request: ExpeditionRequest = {islandId: 1, ships: []};
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

}
