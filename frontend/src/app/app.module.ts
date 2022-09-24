import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { DndModule } from 'ngx-drag-drop';

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
import { LoginComponent } from './user/login/login.component';
import { RegisterComponent } from './user/register/register.component';
import { ErrorInterceptor } from './user/error.interceptor';
import { EquipmentComponent } from './equipment/equipment/equipment.component';
import { ShopComponent } from './shop/shop/shop.component';
import { CitiesComponent } from './city/cities/cities.component';
import { CityComponent } from './city/city/city.component';
import { CitiesListComponent } from './city/cities-list/cities-list.component';
import { HeatmapComponent } from './stats/heatmap/heatmap.component';
import { OverallStatsComponent } from './stats/overall-stats/overall-stats.component';
import { ContextStatsComponent } from './stats/context-stats/context-stats.component';
import { HabitStatsComponent } from './stats/habit-stats/habit-stats.component';

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
    HeroComponent,
    LoginComponent,
    RegisterComponent,
    EquipmentComponent,
    ShopComponent,
    CitiesComponent,
    CityComponent,
    CitiesListComponent,
    HeatmapComponent,
    OverallStatsComponent,
    ContextStatsComponent,
    HabitStatsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    DndModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
