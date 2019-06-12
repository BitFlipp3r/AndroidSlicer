import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AndroidSlicerSharedModule } from 'app/shared';
import {
  SlicerOptionComponent,
  SlicerOptionDetailComponent,
  SlicerOptionUpdateComponent,
  SlicerOptionDeletePopupComponent,
  SlicerOptionDeleteDialogComponent,
  slicerOptionRoute,
  slicerOptionPopupRoute
} from './';

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
  entryComponents: [
    SlicerOptionComponent,
    SlicerOptionUpdateComponent,
    SlicerOptionDeleteDialogComponent,
    SlicerOptionDeletePopupComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AndroidSlicerSlicerOptionModule {}
