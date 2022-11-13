import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CityService } from '../city.service';

@Component({
  selector: 'app-send-expedition',
  templateUrl: './send-expedition.component.html',
  styleUrls: ['./send-expedition.component.css']
})
export class SendExpeditionComponent implements OnInit {
  cityId?: number;

  constructor(private cityService: CityService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      this.cityId = routeParams['id'];
      if(this.cityId) {
        this.getExpeditions(this.cityId);
      } 
    }); 
  }
  
  getExpeditions(cityId: number) {
    throw new Error('Method not implemented.');
  }

}
