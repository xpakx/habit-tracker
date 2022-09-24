import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { StatsResponse } from '../dto/stats-response';
import { StatsService } from '../stats.service';

@Component({
  selector: 'app-context-stats',
  templateUrl: './context-stats.component.html',
  styleUrls: ['./context-stats.component.css']
})
export class ContextStatsComponent implements OnInit {
  stats?: StatsResponse;

  constructor(private statsService: StatsService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe(routeParams => {
      let id: number | undefined = routeParams['id'];
      if(id) {
        this.getStats(routeParams['id']);
      }
    }); 
  }

  private getStats(id: number) {
    this.statsService.getStatsForContext(id).subscribe({
      next: (response: StatsResponse) => this.onSuccess(response),
      error: (error: HttpErrorResponse) => this.onError(error)
    });
  }

  onSuccess(response: StatsResponse) {
    this.stats = response;
  }

  onError(error: HttpErrorResponse) {

  }
}
