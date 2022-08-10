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
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ContextModalComponent } from './habit/context-modal/context-modal.component';
import { DailyContextViewComponent } from './habit/daily-context-view/daily-context-view.component';
import { ContextViewComponent } from './habit/context-view/context-view.component';
import { ContextsViewComponent } from './contexts-view/contexts-view.component';
import { HeroComponent } from './hero/hero/hero.component';

@NgModule({
  declarations: [
    AppComponent,
    DailyViewComponent,
    HabitListComponent,
    HabitComponent,
    MenuComponent,
    HabitModalComponent,
    AddButtonComponent,
    ContextModalComponent,
    DailyContextViewComponent,
    ContextViewComponent,
    ContextsViewComponent,
    HeroComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
