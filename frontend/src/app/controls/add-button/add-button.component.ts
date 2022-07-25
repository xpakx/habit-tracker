import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-add-button',
  templateUrl: './add-button.component.html',
  styleUrls: ['./add-button.component.css']
})
export class AddButtonComponent implements OnInit {
  @Output("clicked") clickEvent = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit(): void {
  }

  clicked(): void {
    this.clickEvent.emit(true);
  }

}
