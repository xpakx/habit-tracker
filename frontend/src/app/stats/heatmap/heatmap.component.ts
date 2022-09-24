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
  }

}
