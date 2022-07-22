import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DailyViewComponent } from './habit/daily-view/daily-view.component';

const routes: Routes = [
  { path: '', component: DailyViewComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
