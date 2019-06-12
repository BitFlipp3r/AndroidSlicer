import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AndroidSlicerSharedModule } from 'app/shared';
import {
  SliceComponent,
  SliceDetailComponent,
  SliceUpdateComponent,
  SliceDeletePopupComponent,
  SliceDeleteDialogComponent,
  sliceRoute,
  slicePopupRoute
} from './';

const ENTITY_STATES = [...sliceRoute, ...slicePopupRoute];

@NgModule({
  imports: [AndroidSlicerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [SliceComponent, SliceDetailComponent, SliceUpdateComponent, SliceDeleteDialogComponent, SliceDeletePopupComponent],
  entryComponents: [SliceComponent, SliceUpdateComponent, SliceDeleteDialogComponent, SliceDeletePopupComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AndroidSlicerSliceModule {}
