import { Component, inject } from '@angular/core';
import { TooltipService } from '../tooltip-service';

@Component({
  selector: 'app-tooltip',
  imports: [],
  templateUrl: './tooltip.html',
  styleUrl: './tooltip.css',
})
export class Tooltip {
  tooltip = inject(TooltipService);
}
