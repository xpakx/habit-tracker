import { Component, DOCUMENT, Inject, NgZone, Renderer2, signal, viewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TooltipService } from './tooltip/tooltip-service';
import { Tooltip } from './tooltip/tooltip/tooltip';
import { Topbar } from './nav/topbar/topbar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Tooltip, Topbar],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');

  private unlistenMouseOver?: () => void;
  private unlistenMouseOut?: () => void;

  constructor(
    @Inject(DOCUMENT) private document: Document,
    private renderer: Renderer2,
    private tooltipService: TooltipService,
    private zone: NgZone
  ) {}

  ngOnInit() {
    this.zone.runOutsideAngular(() => {
      this.unlistenMouseOver = this.renderer.listen(this.document, 'mouseover', (event: MouseEvent) => {
        const target = event.target as HTMLElement;
        const tooltipElement = target.closest('[data-tooltip]') as HTMLElement;
        if (tooltipElement) this.tooltipService.openFor(tooltipElement,event);
      });

      this.unlistenMouseOut = this.renderer.listen(this.document, 'mouseout', (event: MouseEvent) => {
        const target = event.target as HTMLElement;
        const tooltipElement = target.closest('[data-tooltip]') as HTMLElement;
        if (tooltipElement) this.tooltipService.closeFor(tooltipElement, event);

      });
    });
  }

  ngOnDestroy() {
    this.unlistenMouseOver?.();
    this.unlistenMouseOut?.();
  }
}
