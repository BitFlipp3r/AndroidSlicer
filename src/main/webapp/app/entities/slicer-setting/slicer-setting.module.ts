import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AndroidSlicerSharedModule } from 'app/shared/shared.module';
import { SlicerSettingComponent } from './slicer-setting.component';
import { SlicerSettingDetailComponent } from './slicer-setting-detail.component';
import { SlicerSettingUpdateComponent } from './slicer-setting-update.component';
import { SlicerSettingDeletePopupComponent, SlicerSettingDeleteDialogComponent } from './slicer-setting-delete-dialog.component';
import { slicerSettingRoute, slicerSettingPopupRoute } from './slicer-setting.route';

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
  entryComponents: [SlicerSettingDeleteDialogComponent]
})
export class AndroidSlicerSlicerSettingModule {}
