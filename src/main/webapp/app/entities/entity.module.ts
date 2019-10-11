import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'slices',
        loadChildren: () => import('./slice/slice.module').then(m => m.AndroidSlicerSliceModule)
      },
      {
        path: 'slicer-settings',
        loadChildren: () => import('./slicer-setting/slicer-setting.module').then(m => m.AndroidSlicerSlicerSettingModule)
      },
      {
        path: 'slicer-options',
        loadChildren: () => import('./slicer-option/slicer-option.module').then(m => m.AndroidSlicerSlicerOptionModule)
      },
      {
        path: 'cfa-options',
        loadChildren: () => import('./cfa-option/cfa-option.module').then(m => m.AndroidSlicerCFAOptionModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class AndroidSlicerEntityModule {}
