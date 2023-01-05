import { Component, OnInit } from '@angular/core';
import { BattleService } from '../battle.service';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit {
  width?: number;
  height?: number;

  constructor(battleService: BattleService) { }

  ngOnInit(): void {
  }

}
