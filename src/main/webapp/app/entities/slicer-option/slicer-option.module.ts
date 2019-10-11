import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AndroidSlicerSharedModule } from 'app/shared/shared.module';
import { SlicerOptionComponent } from './slicer-option.component';
import { SlicerOptionDetailComponent } from './slicer-option-detail.component';
import { SlicerOptionUpdateComponent } from './slicer-option-update.component';
import { SlicerOptionDeletePopupComponent, SlicerOptionDeleteDialogComponent } from './slicer-option-delete-dialog.component';
import { slicerOptionRoute, slicerOptionPopupRoute } from './slicer-option.route';

const ENTITY_STATES = [...slicerOptionRoute, ...slicerOptionPopupRoute];

@NgModule({
  imports: [AndroidSlicerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    SlicerOptionComponent,
    SlicerOptionDetailComponent,
    SlicerOptionUpdateComponent,
    SlicerOptionDeleteDialogComponent,
    SlicerOptionDeletePopupComponent
  ],
  entryComponents: [SlicerOptionDeleteDialogComponent]
})
export class AndroidSlicerSlicerOptionModule {}
