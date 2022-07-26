import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ItemResponse } from '../dto/item-response';
import { ShopEntry } from '../dto/shop-entry';
import { ShopResponse } from '../dto/shop-response';
import { ShopService } from '../shop.service';

@Component({
  selector: 'app-shop',
  templateUrl: './shop.component.html',
  styleUrls: ['./shop.component.css']
})
export class ShopComponent implements OnInit {
  items: ShopEntry[] = [];

  constructor(private shopService: ShopService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      let id: number | undefined = routeParams['id'];
      if(id) {
        this.getShop(routeParams['id']);
      }
    }); 
  }
  
  getShop(id: number) {
    this.shopService.getShop(id).subscribe({
      next: (response: ShopResponse) => this.saveShop(response)
    });
  }

  saveShop(response: ShopResponse): void {
    this.items = response.items;
  }

  buy(itemId: number, amount: number = 1): void {
    this.shopService.buy({amount: amount}, itemId).subscribe({
      next: (response: ItemResponse) => this.afterBuy(response)
    });
  }

  afterBuy(response: ItemResponse): void {
    //TODO
  }
}
