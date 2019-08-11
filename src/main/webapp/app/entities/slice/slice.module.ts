import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AndroidSlicerSharedModule } from 'app/shared';
import {
  SliceComponent,
  SliceDetailComponent,
  SliceMakeComponent,
  SliceDeletePopupComponent,
  SliceDeleteDialogComponent,
  sliceRoute,
  slicePopupRoute
} from './';

const ENTITY_STATES = [...sliceRoute, ...slicePopupRoute];

import { InputSwitchModule } from 'primeng/inputswitch';
import { DropdownModule } from 'primeng/dropdown';
import { CheckboxModule } from 'primeng/checkbox';
import { SpinnerModule } from 'primeng/spinner';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { MonacoEditorModule } from 'ngx-monaco-editor';

@NgModule({
  imports: [
    AndroidSlicerSharedModule,
    RouterModule.forChild(ENTITY_STATES),
    InputSwitchModule,
    DropdownModule,
    CheckboxModule,
    AutoCompleteModule,
    SpinnerModule,
    MonacoEditorModule
  ],
  declarations: [SliceComponent, SliceDetailComponent, SliceMakeComponent, SliceDeleteDialogComponent, SliceDeletePopupComponent],
  entryComponents: [SliceComponent, SliceMakeComponent, SliceDeleteDialogComponent, SliceDeletePopupComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AndroidSlicerSliceModule {}
