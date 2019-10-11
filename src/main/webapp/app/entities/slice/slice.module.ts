import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AndroidSlicerSharedModule } from 'app/shared/shared.module';
import { SliceComponent } from './slice.component';
import { SliceDetailComponent } from './slice-detail.component';
import { SliceUpdateComponent } from './slice-update.component';
import { SliceDeletePopupComponent, SliceDeleteDialogComponent } from './slice-delete-dialog.component';
import { sliceRoute, slicePopupRoute } from './slice.route';

const ENTITY_STATES = [...sliceRoute, ...slicePopupRoute];

@NgModule({
  imports: [AndroidSlicerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [SliceComponent, SliceDetailComponent, SliceUpdateComponent, SliceDeleteDialogComponent, SliceDeletePopupComponent],
  entryComponents: [SliceDeleteDialogComponent]
})
export class AndroidSlicerSliceModule {}
