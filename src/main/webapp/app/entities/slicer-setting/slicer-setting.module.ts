import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AndroidSlicerSharedModule } from 'app/shared';
import {
  SlicerSettingComponent,
  SlicerSettingDetailComponent,
  SlicerSettingUpdateComponent,
  SlicerSettingDeletePopupComponent,
  SlicerSettingDeleteDialogComponent,
  slicerSettingRoute,
  slicerSettingPopupRoute
} from './';

const ENTITY_STATES = [...slicerSettingRoute, ...slicerSettingPopupRoute];

@NgModule({
  imports: [AndroidSlicerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    SlicerSettingComponent,
    SlicerSettingDetailComponent,
    SlicerSettingUpdateComponent,
    SlicerSettingDeleteDialogComponent,
    SlicerSettingDeletePopupComponent
  ],
  entryComponents: [
    SlicerSettingComponent,
    SlicerSettingUpdateComponent,
    SlicerSettingDeleteDialogComponent,
    SlicerSettingDeletePopupComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AndroidSlicerSlicerSettingModule {}
