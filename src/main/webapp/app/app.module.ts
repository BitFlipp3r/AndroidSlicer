import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { AndroidSlicerSharedModule } from 'app/shared/shared.module';
import { AndroidSlicerCoreModule } from 'app/core/core.module';
import { AndroidSlicerAppRoutingModule } from './app-routing.module';
import { AndroidSlicerHomeModule } from './home/home.module';
import { AndroidSlicerEntityModule } from './entities/entity.module';

import { MonacoEditorModule, NgxMonacoEditorConfig } from 'ngx-monaco-editor';

// jhipster-needle-angular-add-module-import JHipster will add new module here
import { JhiMainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

const monacoConfig: NgxMonacoEditorConfig = {
  defaultOptions: { quickSuggestions: true, scrollBeyondLastLine: false, readOnly: true } // pass default options to be used
};

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AndroidSlicerSharedModule,
    AndroidSlicerCoreModule,
    AndroidSlicerHomeModule,
    MonacoEditorModule.forRoot(monacoConfig),
    // jhipster-needle-angular-add-module JHipster will add new module here
    AndroidSlicerEntityModule,
    AndroidSlicerAppRoutingModule
  ],
  declarations: [JhiMainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [JhiMainComponent]
})
export class AndroidSlicerAppModule {}
