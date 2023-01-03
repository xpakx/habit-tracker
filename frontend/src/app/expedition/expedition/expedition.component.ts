import { Component, Input, OnInit } from '@angular/core';
import { ExpeditionSummary } from '../dto/expedition-summary';

@Component({
  selector: 'app-expedition',
  templateUrl: './expedition.component.html',
  styleUrls: ['./expedition.component.css']
})
export class ExpeditionComponent implements OnInit {
  @Input("expedition") expedition?: ExpeditionSummary;

  constructor() { }

  ngOnInit(): void {
  }

}
