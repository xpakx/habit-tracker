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
    {dayOfYear: 5, completions: 2},
    {dayOfYear: 1, completions:5},
    {dayOfYear: 2, completions:3},
    {dayOfYear: 7, completions:11},
    {dayOfYear: 20, completions:40},
    {dayOfYear: 21, completions:16},
    {dayOfYear: 100, completions: 2},
    {dayOfYear: 365, completions: 13},
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
