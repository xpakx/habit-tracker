import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BattleComponent } from './battle/battle/battle.component';
import { CitiesComponent } from './city/cities/cities.component';
import { CityComponent } from './city/city/city.component';
import { SendExpeditionComponent } from './city/send-expedition/send-expedition.component';
import { ContextsViewComponent } from './contexts-view/contexts-view.component';
import { EquipmentComponent } from './equipment/equipment/equipment.component';
import { ExpeditionListComponent } from './expedition/expedition-list/expedition-list.component';
import { ContextViewComponent } from './habit/context-view/context-view.component';
import { DailyContextViewComponent } from './habit/daily-context-view/daily-context-view.component';
import { DailyViewComponent } from './habit/daily-view/daily-view.component';
import { ShopListComponent } from './shop/shop-list/shop-list.component';
import { ShopComponent } from './shop/shop/shop.component';
import { ContextStatsComponent } from './stats/context-stats/context-stats.component';
import { HabitStatsComponent } from './stats/habit-stats/habit-stats.component';
import { OverallStatsComponent } from './stats/overall-stats/overall-stats.component';
import { LoginComponent } from './user/login/login.component';
import { RegisterComponent } from './user/register/register.component';

const routes: Routes = [
  { path: '', component: DailyViewComponent },
  { path: 'context/:id/daily', component: DailyContextViewComponent },
  { path: 'context/:id', component: ContextViewComponent },
  { path: 'contexts', component: ContextsViewComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'equipment', component: EquipmentComponent },
  { path: 'shop', component: ShopListComponent },
  { path: 'shop/:id', component: ShopComponent },
  { path: 'city', component: CitiesComponent },
  { path: 'city/:id', component: CityComponent },
  { path: 'city/:id/expeditions', component: SendExpeditionComponent },

  { path: 'stats/context/:id', component: ContextStatsComponent },
  { path: 'stats/habit/:id', component: HabitStatsComponent },
  { path: 'stats', component: OverallStatsComponent },
  { path: 'expedition', component: ExpeditionListComponent },
  { path: 'expedition/:id/battle', component: BattleComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
