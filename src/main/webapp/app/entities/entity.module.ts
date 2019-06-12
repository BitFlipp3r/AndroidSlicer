import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'slice',
        loadChildren: './slice/slice.module#AndroidSlicerSliceModule'
      },
      {
        path: 'slicer-setting',
        loadChildren: './slicer-setting/slicer-setting.module#AndroidSlicerSlicerSettingModule'
      },
      {
        path: 'slicer-option',
        loadChildren: './slicer-option/slicer-option.module#AndroidSlicerSlicerOptionModule'
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ],
  declarations: [],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AndroidSlicerEntityModule {}
