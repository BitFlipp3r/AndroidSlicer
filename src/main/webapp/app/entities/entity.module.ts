import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'slice',
        loadChildren: () => import('./slice/slice.module').then(m => m.AndroidSlicerSliceModule)
      },
      {
        path: 'slicer-setting',
        loadChildren: () => import('./slicer-setting/slicer-setting.module').then(m => m.AndroidSlicerSlicerSettingModule)
      },
      {
        path: 'slicer-option',
        loadChildren: () => import('./slicer-option/slicer-option.module').then(m => m.AndroidSlicerSlicerOptionModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class AndroidSlicerEntityModule {}
