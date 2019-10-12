import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AndroidSlicerSharedModule } from 'app/shared/shared.module';
import { InputSwitchModule } from 'primeng/inputswitch';
import { CFAOptionComponent, CFAOptionDetailComponent, cFAOptionRoute, CFAOptionUpdateComponent } from './';

const ENTITY_STATES = [...cFAOptionRoute];

@NgModule({
  imports: [AndroidSlicerSharedModule, RouterModule.forChild(ENTITY_STATES), InputSwitchModule],
  declarations: [CFAOptionComponent, CFAOptionDetailComponent, CFAOptionUpdateComponent],
  entryComponents: [CFAOptionComponent, CFAOptionUpdateComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AndroidSlicerCFAOptionModule {}
