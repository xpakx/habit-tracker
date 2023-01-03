import { Component, OnInit } from '@angular/core';
import { ExpeditionSummary } from '../dto/expedition-summary';
import { ExpeditionService } from '../expedition.service';

@Component({
  selector: 'app-expedition-list',
  templateUrl: './expedition-list.component.html',
  styleUrls: ['./expedition-list.component.css']
})
export class ExpeditionListComponent implements OnInit {
  expeditions: ExpeditionSummary[] = [];

  constructor(private expeditionService: ExpeditionService) { }

  ngOnInit(): void {
    this.expeditionService.getActiveExpeditions().subscribe({
      next: (response: ExpeditionSummary[]) => this.saveExpeditions(response)
    });
  }

  saveExpeditions(response: ExpeditionSummary[]): void {
    this.expeditions = response;
  }

}
