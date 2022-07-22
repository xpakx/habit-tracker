import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DailyViewComponent } from './habit/daily-view/daily-view.component';
import { HabitListComponent } from './habit/habit-list/habit-list.component';
import { HabitComponent } from './habit/habit/habit.component';

@NgModule({
  declarations: [
    AppComponent,
    DailyViewComponent,
    HabitListComponent,
    HabitComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
