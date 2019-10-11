import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AndroidSlicerSharedModule } from 'app/shared/shared.module';

import { JhiDocsComponent } from './docs.component';

import { docsRoute } from './docs.route';

@NgModule({
  imports: [AndroidSlicerSharedModule, RouterModule.forChild([docsRoute])],
  declarations: [JhiDocsComponent]
})
export class DocsModule {}
