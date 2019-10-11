import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AndroidSlicerSharedModule } from 'app/shared/shared.module';

import { LogsComponent } from './logs.component';

import { logsRoute } from './logs.route';

@NgModule({
  imports: [AndroidSlicerSharedModule, RouterModule.forChild([logsRoute])],
  declarations: [LogsComponent]
})
export class LogsModule {}
