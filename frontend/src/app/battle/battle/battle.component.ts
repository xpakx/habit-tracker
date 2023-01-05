import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BattleService } from '../battle.service';
import { BattleResponse } from '../dto/battle-response';

@Component({
  selector: 'app-battle',
  templateUrl: './battle.component.html',
  styleUrls: ['./battle.component.css']
})
export class BattleComponent implements OnInit {
  battle?: BattleResponse;

  constructor(private battleService: BattleService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      let id: number | undefined = routeParams['id'];
      if(id) {
        this.getBattle(routeParams['id']);
      }
    }); 
  }

  getBattle(id: any) {
    this.battleService.startBattle(id).subscribe({
      next: (result: BattleResponse) => this.saveBattle(result),
      error: (error: HttpErrorResponse) => this.onError(error)
    })
  }

  saveBattle(result: BattleResponse): void {
    this.battle = result;
  }

  onError(error: HttpErrorResponse): void {
    throw new Error('Method not implemented.');
  }

}
