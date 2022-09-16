import { Component, OnInit } from '@angular/core';
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

  constructor(private eqService: EquipmentService) { }

  ngOnInit(): void {
    this.eqService.getEquipment().subscribe({
      next: (response: EquipmentResponse) => this.saveEquipment(response)
    });
  }

  saveEquipment(response: EquipmentResponse): void {
    this.items = response.items;
  }
}
