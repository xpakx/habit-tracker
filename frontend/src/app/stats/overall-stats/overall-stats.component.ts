import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { StatsResponse } from '../dto/stats-response';
import { StatsService } from '../stats.service';

@Component({
  selector: 'app-overall-stats',
  templateUrl: './overall-stats.component.html',
  styleUrls: ['./overall-stats.component.css']
})
export class OverallStatsComponent implements OnInit {
  stats?: StatsResponse = {completions:9, days: [
    {date: new Date(2022, 1, 1), completions: 2},
    {date: new Date(2022, 1, 2), completions:5},
    {date: new Date(2022, 1, 3), completions:3},
    {date: new Date(2022, 1, 7), completions:11},
    {date: new Date(2022, 1, 20), completions:40},
    {date: new Date(2022, 1, 21), completions:16},
    {date: new Date(2022, 3, 7), completions: 2},
    {date: new Date(2022, 12, 7), completions: 13},
  ]};

  constructor(private statsService: StatsService) { }

  ngOnInit(): void {
    this.statsService.getStats().subscribe({
      next: (response: StatsResponse) => this.onSuccess(response),
      error: (error: HttpErrorResponse) => this.onError(error)
    })
  }

  onSuccess(response: StatsResponse) {
    this.stats = response;
  }

  onError(error: HttpErrorResponse) {

  }
}
