import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ContextsViewComponent } from './contexts-view/contexts-view.component';
import { ContextViewComponent } from './habit/context-view/context-view.component';
import { DailyContextViewComponent } from './habit/daily-context-view/daily-context-view.component';
import { DailyViewComponent } from './habit/daily-view/daily-view.component';

const routes: Routes = [
  { path: '', component: DailyViewComponent },
  { path: 'context/:id/daily', component: DailyContextViewComponent },
  { path: 'context/:id', component: ContextViewComponent },
  { path: 'contexts', component: ContextsViewComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
