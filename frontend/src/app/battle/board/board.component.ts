import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BattlePosition } from '../dto/battle-position';
import { BattleResponse } from '../dto/battle-response';
import { BattleShip } from '../dto/battle-ship';
import { MoveEvent } from '../dto/move-event';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit {
  @Input("battle") battle?: BattleResponse;
  @Input("placement") shipToPlace: boolean = false;
  @Output("place") placement = new EventEmitter<BattlePosition>();
  @Output("move") movement = new EventEmitter<MoveEvent>();
  @Output("attack") attack = new EventEmitter<MoveEvent>();
  shipToMoveId?: number;

  constructor() { }

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

  isShip(x: number, y: number): boolean {
    if(this.battle) {
      for(let ship of this.battle.ships) {
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
    if(this.shipToMoveId) {
      if(this.isEnemy(x,y)) {
        this.attack.emit({position: {x: x, y: y}, shipId: this.shipToMoveId});
      } else {
        this.movement.emit({position: {x: x, y: y}, shipId: this.shipToMoveId});
      }
      
      this.shipToMoveId = undefined;
    }
  }


  action(x: number, y: number) {
    if(this.shipToPlace) {
      this.placeShip(x, y);
    } else if(this.shipToMoveId) {
      this.moveShip(x, y);
    } else if(this.isShip(x, y)) {
      this.shipToMoveId = this.getShipId(x, y)
    }
  }


  private getShipId(x: number, y: number): number | undefined {
    let ship: BattleShip | undefined = this.battle?.ships.find(a => a.position.x == x && a.position.y == y)
    return ship ? ship.id : undefined;
  }
}
