import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DailyContextViewComponent } from './habit/daily-context-view/daily-context-view.component';
import { DailyViewComponent } from './habit/daily-view/daily-view.component';

const routes: Routes = [
  { path: '', component: DailyViewComponent },
  { path: 'context/:id', component: DailyContextViewComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
