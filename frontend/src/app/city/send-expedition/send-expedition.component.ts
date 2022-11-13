import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CityService } from '../city.service';
import { DeployedShip } from '../dto/deployed-ship';

@Component({
  selector: 'app-send-expedition',
  templateUrl: './send-expedition.component.html',
  styleUrls: ['./send-expedition.component.css']
})
export class SendExpeditionComponent implements OnInit {
  cityId?: number;
  showShips: boolean = false;
  ships: DeployedShip[] = [];

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

}
