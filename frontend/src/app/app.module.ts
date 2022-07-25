import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DailyViewComponent } from './habit/daily-view/daily-view.component';
import { HabitListComponent } from './habit/habit-list/habit-list.component';
import { HabitComponent } from './habit/habit/habit.component';
import { MenuComponent } from './navigation/menu/menu.component';
import { HabitModalComponent } from './habit/habit-modal/habit-modal.component';
import { AddButtonComponent } from './controls/add-button/add-button.component';

@NgModule({
  declarations: [
    AppComponent,
    DailyViewComponent,
    HabitListComponent,
    HabitComponent,
    MenuComponent,
    HabitModalComponent,
    AddButtonComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
