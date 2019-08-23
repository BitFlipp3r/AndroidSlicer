import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AndroidSlicerSharedModule } from 'app/shared';
import { SlicerSettingComponent, SlicerSettingDetailComponent, SlicerSettingUpdateComponent, slicerSettingRoute } from './';

const ENTITY_STATES = [...slicerSettingRoute];

@NgModule({
  imports: [AndroidSlicerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [SlicerSettingComponent, SlicerSettingDetailComponent, SlicerSettingUpdateComponent],
  entryComponents: [SlicerSettingComponent, SlicerSettingUpdateComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AndroidSlicerSlicerSettingModule {}
