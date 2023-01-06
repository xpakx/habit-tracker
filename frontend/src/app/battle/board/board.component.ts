import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BattleService } from '../battle.service';
import { BattlePosition } from '../dto/battle-position';
import { BattleResponse } from '../dto/battle-response';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit {
  @Input("battle") battle?: BattleResponse;
  @Input("placement") shipToPlace: boolean = false;
  @Output("place") placement = new EventEmitter<BattlePosition>();
  @Output("move") movement = new EventEmitter<BattlePosition>();

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

  placeShip(x: number, y: number) {
    this.placement.emit({x: x, y: y});
  }

  moveShip(x: number, y: number) {
    this.movement.emit({x: x, y: y});
  }


  action(x: number, y: number) {
    if(this.shipToPlace) {
      this.placeShip(x, y);
    } else {
      this.moveShip(x, y);
    }
  }

}
