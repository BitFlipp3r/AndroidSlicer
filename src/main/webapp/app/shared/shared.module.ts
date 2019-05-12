import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { AndroidSlicerSharedLibsModule, AndroidSlicerSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
  imports: [AndroidSlicerSharedLibsModule, AndroidSlicerSharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [JhiLoginModalComponent],
  exports: [AndroidSlicerSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AndroidSlicerSharedModule {
  static forRoot() {
    return {
      ngModule: AndroidSlicerSharedModule
    };
  }
}
