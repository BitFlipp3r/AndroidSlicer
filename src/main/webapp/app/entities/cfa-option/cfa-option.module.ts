import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AndroidSlicerSharedModule } from 'app/shared';
import { CFAOptionComponent, CFAOptionDetailComponent, CFAOptionUpdateComponent, cFAOptionRoute } from './';
import { InputSwitchModule } from 'primeng/inputswitch';

const ENTITY_STATES = [...cFAOptionRoute];

@NgModule({
  imports: [AndroidSlicerSharedModule, RouterModule.forChild(ENTITY_STATES), InputSwitchModule],
  declarations: [CFAOptionComponent, CFAOptionDetailComponent, CFAOptionUpdateComponent],
  entryComponents: [CFAOptionComponent, CFAOptionUpdateComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AndroidSlicerCFAOptionModule {}
