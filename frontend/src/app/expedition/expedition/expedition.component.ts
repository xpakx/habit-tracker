import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { ActionResponse } from '../dto/action-response';
import { ExpeditionResultResponse } from '../dto/expedition-result-response';
import { ExpeditionSummary } from '../dto/expedition-summary';
import { ExpeditionService } from '../expedition.service';

@Component({
  selector: 'app-expedition',
  templateUrl: './expedition.component.html',
  styleUrls: ['./expedition.component.css']
})
export class ExpeditionComponent implements OnInit {
  @Input("expedition") expedition?: ExpeditionSummary;
  result?: ExpeditionResultResponse;

  constructor(private expeditionService: ExpeditionService) { }

  ngOnInit(): void {
  }

  loadResult(): void {
    if(this.expedition) {
      this.expeditionService.getResult(this.expedition.id).subscribe({
        next: (response: ExpeditionResultResponse) => this.showResponse(response),
        error: (error: HttpErrorResponse) => this.onError(error)
      });
    }
  }

  onError(error: HttpErrorResponse): void {
    throw new Error('Method not implemented.');
  }

  showResponse(response: ExpeditionResultResponse): void {
    this.result = response;
  }

  return() {
    if(this.expedition) {
      this.expeditionService.return({action: true}, this.expedition.id).subscribe({
        next: (response: ActionResponse) => this.onAction(response),
        error: (error: HttpErrorResponse) => this.onError(error)
      });
    }
  }

  complete() {
    if(this.expedition) {
      this.expeditionService.complete({action: true}, this.expedition.id).subscribe({
        next: (response: ActionResponse) => this.onAction(response),
        error: (error: HttpErrorResponse) => this.onError(error)
      });
    }
  }

  onAction(response: ActionResponse): void {
    throw new Error('Method not implemented.');
  }
}
