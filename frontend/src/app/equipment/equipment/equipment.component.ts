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
  items: EquipmentEntry[] = [{id: 1, name: "wood", amount: 50}];
  draggedItem?: EquipmentEntry;
  empty: EquipmentEntry = {id:-1, name: "", amount: 0}
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

  craft(): void {
    this.recipeService.craft(this.createRequest()).subscribe({
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

  createRequest(): CraftRequest {
    return {
      amount: 1, 
      elem1: {id: this.craftSlots[0].id},
      elem2: {id: this.craftSlots[1].id},
      elem3: {id: this.craftSlots[2].id},
      elem4: {id: this.craftSlots[3].id},
      elem5: {id: this.craftSlots[4].id},
      elem6: {id: this.craftSlots[5].id},
      elem7: {id: this.craftSlots[6].id},
      elem8: {id: this.craftSlots[7].id},
      elem9: {id: this.craftSlots[8].id},
    }
  }
}
