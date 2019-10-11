import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { InputSwitchModule } from 'primeng/inputswitch';
import { AndroidSlicerSharedModule } from 'app/shared/shared.module';
import { SlicerOptionComponent } from './slicer-option.component';
import { SlicerOptionDetailComponent } from './slicer-option-detail.component';
import { SlicerOptionUpdateComponent } from './slicer-option-update.component';
import { slicerOptionRoute } from './slicer-option.route';

const ENTITY_STATES = [...slicerOptionRoute];

@NgModule({
  imports: [AndroidSlicerSharedModule, RouterModule.forChild(ENTITY_STATES), InputSwitchModule],
  declarations: [SlicerOptionComponent, SlicerOptionDetailComponent, SlicerOptionUpdateComponent]
})
export class AndroidSlicerSlicerOptionModule {}
