import { Component, Input, OnInit } from '@angular/core';
import { Day } from '../dto/day';

@Component({
  selector: 'app-heatmap',
  templateUrl: './heatmap.component.html',
  styleUrls: ['./heatmap.component.css']
})
export class HeatmapComponent implements OnInit {
  @Input() days: Day[] = [];
  @Input() completions: number = 0;

  constructor() { }

  ngOnInit(): void {
    this.days[0].date.getDate()
  }

  dateDiff(date2: Date, date1: Date): number {
    return Math.floor(
      (
        Date.UTC(date2.getFullYear(), date2.getMonth(), date2.getDate()) - 
        Date.UTC(date1.getFullYear(), date1.getMonth(), date1.getDate()) 
      ) 
      /(1000 * 60 * 60 * 24
    ));
  }

  dayPlusDays(date: Date, days: number): Date {
    var date = new Date(date.valueOf());
    date.setDate(date.getDate() + days);
    return date;
  }

}