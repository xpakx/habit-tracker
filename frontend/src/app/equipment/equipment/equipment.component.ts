import { Component, OnInit } from '@angular/core';
import { DndDropEvent } from 'ngx-drag-drop'
import { EquipmentEntry } from '../dto/equipment-entry';
import { EquipmentResponse } from '../dto/equipment-response';
import { EquipmentService } from '../equipment.service';

@Component({
  selector: 'app-equipment',
  templateUrl: './equipment.component.html',
  styleUrls: ['./equipment.component.css']
})
export class EquipmentComponent implements OnInit {
  items: EquipmentEntry[] = [{id: 1, name: "wood", amount: 50}];
  draggedItem?: number;
  craftSlots: number[] = [-1,-1,-1,-1,-1,-1,-1,-1,-1];

  constructor(private eqService: EquipmentService) { }

  ngOnInit(): void {
    this.eqService.getEquipment().subscribe({
      next: (response: EquipmentResponse) => this.saveEquipment(response)
    });
  }

  saveEquipment(response: EquipmentResponse): void {
    this.items = response.items;
  }

  onDrop(event: DndDropEvent, num: number) {
    if(this.draggedItem) {
      this.craftSlots[num] = this.draggedItem;
    }
  }

  onDragStart(id: number) {
    this.draggedItem = id;
  }

  onDragEnd() {
    this.draggedItem = undefined;
  }

  reset(num: number) {
    this.craftSlots[num] = -1;
  }
}
