import { Component, Input, OnInit } from '@angular/core';
import { BattleService } from '../battle.service';
import { BattleResponse } from '../dto/battle-response';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit {
  @Input("battle") battle?: BattleResponse;

  constructor(battleService: BattleService) { }

  ngOnInit(): void {
  }


  getImage(x: number, y: number): String {
    if(this.battle) {
      for(let ship of this.battle.ships) {
        if(ship.position.x == x && ship.position.y == y) {
          return ship.code;
        }
      }
      for(let ship of this.battle.enemyShips) {
        if(ship.position.x == x && ship.position.y == y) {
          return ship.code;
        }
      }

    }
    return 'SEA';
  }

  isEnemy(x: number, y: number): boolean {
    if(this.battle) {
      for(let ship of this.battle.enemyShips) {
        if(ship.position.x == x && ship.position.y == y) {
          return true;
        }
      }

    }
    return false;
  }

}
