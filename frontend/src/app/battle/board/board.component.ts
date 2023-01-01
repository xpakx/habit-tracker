import { Component, OnInit } from '@angular/core';
import { Battleservice } from '../battle.service';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit {
  width?: number;
  height?: number;

  constructor(battleService: Battleservice) { }

  ngOnInit(): void {
  }

}
