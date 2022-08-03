import { Component, OnInit } from '@angular/core';
import { ContextService } from '../habit/context.service';
import { ContextDetails } from '../habit/dto/context-details';

@Component({
  selector: 'app-contexts-view',
  templateUrl: './contexts-view.component.html',
  styleUrls: ['./contexts-view.component.css']
})
export class ContextsViewComponent implements OnInit {
  contexts: ContextDetails[] = [];
  showContextModal: boolean = false;

  constructor(private contextService: ContextService) { }

  ngOnInit(): void {
    this.contextService.getContexts().subscribe({
      next: (response: ContextDetails[]) => this.saveContexts(response)
    });
  }

  saveContexts(response: ContextDetails[]): void {
    this.contexts = response;
  }

  displayContextModal(): void {
    this.showContextModal = true;
  }
}
