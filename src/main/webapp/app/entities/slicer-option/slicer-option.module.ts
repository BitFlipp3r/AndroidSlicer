import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AndroidSlicerSharedModule } from 'app/shared';
import { SlicerOptionComponent, SlicerOptionDetailComponent, SlicerOptionUpdateComponent, slicerOptionRoute } from './';
import { InputSwitchModule } from 'primeng/inputswitch';

const ENTITY_STATES = [...slicerOptionRoute];

@NgModule({
  imports: [AndroidSlicerSharedModule, RouterModule.forChild(ENTITY_STATES), InputSwitchModule],
  declarations: [SlicerOptionComponent, SlicerOptionDetailComponent, SlicerOptionUpdateComponent],
  entryComponents: [SlicerOptionComponent, SlicerOptionUpdateComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AndroidSlicerSlicerOptionModule {}
