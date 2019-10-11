import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AndroidSlicerSharedModule } from 'app/shared/shared.module';
import { SliceComponent } from './slice.component';
import { SliceDetailComponent } from './slice-detail.component';
import { SliceMakeComponent } from './slice-make.component';
import { SliceDeletePopupComponent, SliceDeleteDialogComponent } from './slice-delete-dialog.component';
import { sliceRoute, slicePopupRoute } from './slice.route';

const ENTITY_STATES = [...sliceRoute, ...slicePopupRoute];

import { InputSwitchModule } from 'primeng/inputswitch';
import { DropdownModule } from 'primeng/dropdown';
import { CheckboxModule } from 'primeng/checkbox';
import { SpinnerModule } from 'primeng/spinner';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { TabMenuModule } from 'primeng/tabmenu';
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
    TabMenuModule,
    MonacoEditorModule
  ],
  declarations: [SliceComponent, SliceDetailComponent, SliceMakeComponent, SliceDeleteDialogComponent, SliceDeletePopupComponent],
  entryComponents: [SliceComponent, SliceMakeComponent, SliceDeleteDialogComponent, SliceDeletePopupComponent]
})
export class AndroidSlicerSliceModule {}
