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
  stats?: StatsResponse;

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
