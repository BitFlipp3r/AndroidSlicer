import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AndroidSlicerSharedModule } from 'app/shared';
import {
  CFAOptionComponent,
  CFAOptionDetailComponent,
  CFAOptionUpdateComponent,
  CFAOptionDeletePopupComponent,
  CFAOptionDeleteDialogComponent,
  cFAOptionRoute,
  cFAOptionPopupRoute
} from './';
import { InputSwitchModule } from 'primeng/inputswitch';

const ENTITY_STATES = [...cFAOptionRoute, ...cFAOptionPopupRoute];

@NgModule({
  imports: [AndroidSlicerSharedModule, RouterModule.forChild(ENTITY_STATES), InputSwitchModule],
  declarations: [
    CFAOptionComponent,
    CFAOptionDetailComponent,
    CFAOptionUpdateComponent,
    CFAOptionDeleteDialogComponent,
    CFAOptionDeletePopupComponent
  ],
  entryComponents: [CFAOptionComponent, CFAOptionUpdateComponent, CFAOptionDeleteDialogComponent, CFAOptionDeletePopupComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AndroidSlicerCFAOptionModule {}
