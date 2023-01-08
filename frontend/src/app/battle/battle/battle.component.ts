import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BattleService } from '../battle.service';
import { BattlePosition } from '../dto/battle-position';
import { BattleResponse } from '../dto/battle-response';
import { BattleShip } from '../dto/battle-ship';
import { MoveEvent } from '../dto/move-event';
import { MoveResponse } from '../dto/move-response';

@Component({
  selector: 'app-battle',
  templateUrl: './battle.component.html',
  styleUrls: ['./battle.component.css']
})
export class BattleComponent implements OnInit {
  battle?: BattleResponse;
  unassignedShips: BattleShip[] = [];
  shipForPlacementId?: number;

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
    this.battleService.getBattle(id).subscribe({
      next: (result: BattleResponse) => this.saveBattle(result),
      error: (error: HttpErrorResponse) => this.onError(error)
    })
  }

  saveBattle(result: BattleResponse): void {
    this.battle = result;
    this.unassignedShips = this.battle.ships.filter(a => !a.prepared);
  }

  onError(error: HttpErrorResponse): void {
    throw new Error('Method not implemented.');
  }


  endTurn(): void {
    if(!this.battle) {
      return;
    }
    this.battleService.endTurn(this.battle.battleId).subscribe({
      next: (result: MoveResponse[]) => this.applyMoves(result),
      error: (error: HttpErrorResponse) => this.onError(error)
    })
  }

  applyMoves(result: MoveResponse[]): void {
    if(!this.battle) {
      return;
    }
    for(let ship of this.battle?.ships) {
      ship.action = false;
      ship.movement = false;
    }
    for(let move of result) {
      this.applyEnemyMove(move);
    }
  }

  applyEnemyMove(move: MoveResponse): void {
    if(move.move) {
      this.onEnemyMove(move);
    } else if(move.attack) {
      this.onEnemyAttack(move);
    }
  }

  chooseShip(shipId: number) {
    this.shipForPlacementId = shipId;
  }

  place(position: BattlePosition): void {
    if(!this.battle || !this.shipForPlacementId) {
      return;
    }
    let shipId = this.shipForPlacementId;
    this.shipForPlacementId = undefined;
    this.battleService.prepare({x: position.x, y: position.y, shipId: shipId, action: 'PREPARE'}, this.battle.battleId).subscribe({
      next: (result: MoveResponse) => this.placeShip(result, shipId, position),
      error: (error: HttpErrorResponse) => this.onError(error)
    })

  }

  placeShip(result: MoveResponse, shipId: number, position: BattlePosition): void {
    if(result.success) {
      this.unassignedShips = this.unassignedShips.filter(a => a.id != shipId);
      let ship: BattleShip | undefined = this.battle?.ships.find(a => a.id == shipId);
      if(ship) {
        ship.position = { x: position.x, y: position.y};
      }
    }
  }


  move(event: MoveEvent) {
    if(!this.battle || !this.shipForPlacementId) {
      return;
    }
    this.shipForPlacementId = undefined;
    this.battleService.move({x: event.position.x, y: event.position.y, shipId: event.shipId, action: 'MOVE'}, this.battle.battleId).subscribe({
      next: (result: MoveResponse) => this.onMove(result),
      error: (error: HttpErrorResponse) => this.onError(error)
    })
  }

  attack(event: MoveEvent) {
    if(!this.battle || !this.shipForPlacementId) {
      return;
    }
    this.shipForPlacementId = undefined;
    this.battleService.move({x: event.position.x, y: event.position.y, shipId: event.shipId, action: 'ATTACK'}, this.battle.battleId).subscribe({
      next: (result: MoveResponse) => this.onAttack(result),
      error: (error: HttpErrorResponse) => this.onError(error)
    })
  }

  onMove(result: MoveResponse): void {
    if(result.move) {
      let shipId =  result.move.shipId
      let ship: BattleShip | undefined = this.battle?.ships.find(a => a.id == shipId);
      if(ship) {
        ship.position = { x: result.move.x, y: result.move.y};
        ship.movement = true;
      }
    }
  }

  onAttack(result: MoveResponse): void {
    if(result.attack) {
      let shipId =  result.attack.shipId
      let position: BattlePosition = {x: result.attack.x, y: result.attack.y}
      let ship: BattleShip | undefined = this.battle?.ships.find(a => a.id == shipId);
      let target: BattleShip | undefined = this.battle?.enemyShips.find(a => a.position.x == position.x && a.position.y == position.y);
      if(ship && target) {
        ship.action = true;
        target.hp = target.hp = result.attack.damage;
        target.damaged = true;
        if(target.hp <= 0) {
          target.destroyed = true;
        }
      }
    }
  }

  onEnemyMove(result: MoveResponse): void {
    if(result.move) {
      let shipId =  result.move.shipId
      let ship: BattleShip | undefined = this.battle?.enemyShips.find(a => a.id == shipId);
      if(ship) {
        ship.position = { x: result.move.x, y: result.move.y};
        ship.movement = true;
      }
    }
  }

  onEnemyAttack(result: MoveResponse): void {
    if(result.attack) {
      let shipId =  result.attack.shipId
      let position: BattlePosition = {x: result.attack.x, y: result.attack.y}
      let ship: BattleShip | undefined = this.battle?.enemyShips.find(a => a.id == shipId);
      let target: BattleShip | undefined = this.battle?.ships.find(a => a.position.x == position.x && a.position.y == position.y);
      if(ship && target) {
        ship.action = true;
        target.hp = target.hp = result.attack.damage;
        target.damaged = true;
        if(target.hp <= 0) {
          target.destroyed = true;
        }
      }
    }
  }

}
