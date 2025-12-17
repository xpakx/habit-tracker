import { Injectable, NgZone, signal } from '@angular/core';

export interface TooltipState {
  visible: boolean;
  text: string;
  x: number;
  y: number;
}


@Injectable({
  providedIn: 'root',
})
export class TooltipService {
  state = signal<TooltipState>({ visible: false, text: '', x: 0, y: 0 });

  constructor(private zone: NgZone) {
  }

  show(text: string, x: number, y: number) {
    this.state.set({ visible: true, text, x, y });
  }

  hide() {
    this.state.update(s => ({ ...s, visible: false }));
  }

  openFor(tooltipElement: HTMLElement, _event: MouseEvent) {
    const text = tooltipElement.getAttribute('data-tooltip');
    if (!text) return;

    const rect = tooltipElement.getBoundingClientRect();
    const x = rect.left + (rect.width / 2);
    const y = rect.top;

    this.zone.run(() => {
      this.show(text, x, y);
    });
  }

  closeFor(tooltipElement: HTMLElement, _event: MouseEvent) {
    this.zone.run(() => {
      this.hide();
    });
  }

}
