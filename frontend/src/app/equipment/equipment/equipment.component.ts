import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { DndDropEvent } from 'ngx-drag-drop'
import { ItemResponse } from 'src/app/shop/dto/item-response';
import { CraftRequest } from '../dto/craft-request';
import { EquipmentEntry } from '../dto/equipment-entry';
import { EquipmentResponse } from '../dto/equipment-response';
import { EquipmentService } from '../equipment.service';
import { RecipeService } from '../recipe.service';

@Component({
  selector: 'app-equipment',
  templateUrl: './equipment.component.html',
  styleUrls: ['./equipment.component.css']
})
export class EquipmentComponent implements OnInit {
  items: EquipmentEntry[] = [];
  draggedItem?: EquipmentEntry;
  empty: EquipmentEntry = {id:-1, itemId: -1, name: "", icon: "", amount: 0}
  craftSlots: EquipmentEntry[] = [
    this.empty, this.empty, this.empty,
    this.empty, this.empty, this.empty,
    this.empty, this.empty, this.empty
  ];

  constructor(private eqService: EquipmentService, private recipeService: RecipeService) { }

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

  onDragStart(id: EquipmentEntry) {
    this.draggedItem = id;
  }

  onDragEnd() {
    this.draggedItem = undefined;
  }

  reset(num: number) {
    this.craftSlots[num] = this.empty;
  }

  craft(amount: number = 1): void {
    this.recipeService.craft(this.createRequest(amount)).subscribe({
      next: (response: ItemResponse) => this.onCraft(response),
      error: (error: HttpErrorResponse) => this.onCraftError(error),
    });
  }

  onCraft(response: ItemResponse): void {
    throw new Error('Method not implemented.');
  }

  onCraftError(error: HttpErrorResponse): void {
    throw new Error('Method not implemented.');
  }

  createRequest(amount: number = 1): CraftRequest {
    return {
      amount: amount, 
      elem1: {id: this.getItemId(0)},
      elem2: {id: this.getItemId(1)},
      elem3: {id: this.getItemId(2)},
      elem4: {id: this.getItemId(3)},
      elem5: {id: this.getItemId(4)},
      elem6: {id: this.getItemId(5)},
      elem7: {id: this.getItemId(6)},
      elem8: {id: this.getItemId(7)},
      elem9: {id: this.getItemId(8)},
    }
  }

  private getItemId(num: number): number | undefined {
    return this.craftSlots[num].itemId > -1 ? this.craftSlots[num].itemId : undefined;
  }
}
