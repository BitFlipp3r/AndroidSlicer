import { NgModule } from '@angular/core';

import { AndroidSlicerSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
  imports: [AndroidSlicerSharedLibsModule],
  declarations: [JhiAlertComponent, JhiAlertErrorComponent],
  exports: [AndroidSlicerSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class AndroidSlicerSharedCommonModule {}
