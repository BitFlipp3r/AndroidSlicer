import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { JhiDataUtils, JhiAlertService } from 'ng-jhipster';

import { ISlice } from 'app/shared/model/slice.model';
import { interval } from 'rxjs';
import { startWith, switchMap, takeWhile } from 'rxjs/operators';
import { SliceService } from '.';
import { AndroidOptionsService } from 'app/shared/services/android-options.service';
import { HttpErrorResponse } from '@angular/common/http';
import { DiffEditorModel } from 'ngx-monaco-editor';

import { MenuItem } from 'primeng/api';

@Component({
  selector: 'jhi-slice-detail',
  templateUrl: './slice-detail.component.html'
})
export class SliceDetailComponent implements OnInit {
  slice: ISlice;

  slicedClassItems: MenuItem[] = [];
  activeItem: MenuItem;

  editorOptions = { theme: 'vs', language: 'java', followsCaret: true, ignoreCharChanges: true };

  private diffEditor: any;
  sideBySide = false;
  diffEditorOptions;

  sliceCodes: string[] = [];
  sourceCodes: string[] = [];
  // keep track of the slice code positions in sliceCodes to bring source codes in the right order after they have loaded
  private classesIndexMap: string[] = [];

  sliceCodeDiffModel: DiffEditorModel;
  sourceFileDiffModel: DiffEditorModel;

  currentSliceIndex: number;

  private poll: boolean;

  isCodeLoadingOrPrecessing = true;
  slicingFinished = false;
  showDiff = false;
  scrollLog = true;

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected activatedRoute: ActivatedRoute,
    private sliceService: SliceService,
    private router: Router,
    protected androidOptionsService: AndroidOptionsService
  ) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ slice }) => {
      this.slice = slice;

      if (slice.running) {
        // update until slicing has finished
        this.refresh();
        this.poll = true;
      } else {
        this.onSlicingFinished();
      }
    });

    // deactivate polling when page is changed
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.poll = false;
      }
    });
  }

  previousState() {
    window.history.back();
  }

  // poll updates every 10 seconds
  private refresh() {
    interval(10000)
      .pipe(
        startWith(0),
        switchMap(() => this.sliceService.find(this.slice.id))
      )
      .pipe(takeWhile(() => this.slice.running && this.poll))
      .subscribe(httpResponse => {
        this.slice = httpResponse.body;

        // scroll log to bottom
        setTimeout(() => {
          // allow time for DOM update
          const logTxt = document.getElementById('logTxt');
          if (this.slice.log && logTxt && logTxt.scrollHeight && this.scrollLog) {
            logTxt.scrollTop = logTxt.scrollHeight;
          }
        }, 100);

        if (!this.slice.running) {
          this.onSlicingFinished();
        }
      });
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  private onSlicingFinished(): void {
    this.slice.slicedClasses.forEach(slicedClass => {
      const slicedPathAndClassName = slicedClass.packagePath + '/' + slicedClass.className;
      if (slicedPathAndClassName === this.slice.androidClassName) {
        // add android main class (i.e. entry class) to first position
        this.sliceCodes.unshift(slicedClass.code);
        this.classesIndexMap.unshift(slicedClass.className);
        this.slicedClassItems.unshift({ title: slicedClass.className, label: slicedPathAndClassName });
      } else {
        this.sliceCodes.push(slicedClass.code);
        this.classesIndexMap.push(slicedClass.className);
        this.slicedClassItems.push({ title: slicedClass.className, label: slicedPathAndClassName });
      }
    });

    this.currentSliceIndex = 0;
    this.activeItem = this.slicedClassItems[0];

    this.loadSourceFiles();

    this.slicingFinished = true;
  }

  onSliceClassSelected(event, index) {
    this.currentSliceIndex = index;
    this.activeItem = this.slicedClassItems[index];
    this.setDiffEditorModels();
    event.preventDefault();
  }

  private loadSourceFiles() {
    this.slice.slicedClasses.forEach(slicedClass => {
      // get source file for comparison
      const slicedPathAndClassName = slicedClass.packagePath + '/' + slicedClass.className;
      this.androidOptionsService.getServiceSource(this.slice.androidVersion, slicedPathAndClassName).subscribe(
        (res: any) => {
          this.sourceCodes[this.classesIndexMap.indexOf(slicedClass.className)] = res.body;
          // remove loading overlay if all source classes are loaded
          if (this.sourceCodes.length === this.sliceCodes.length) {
            this.isCodeLoadingOrPrecessing = false;
          }
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
    });
  }

  onInitDiffEditor(editor) {
    this.diffEditor = editor;
    this.diffEditor.onDidUpdateDiff(() => {
      this.isCodeLoadingOrPrecessing = false;
    });
  }

  updateDiff() {
    if (this.showDiff && this.diffEditor) {
      this.isCodeLoadingOrPrecessing = true;
      this.diffEditorOptions = {
        theme: 'vs',
        language: 'java',
        renderSideBySide: this.sideBySide,
        followsCaret: true,
        ignoreCharChanges: true
      };
      this.diffEditor.updateOptions(this.diffEditorOptions);
    }
  }

  setDiffEditorModels(): void {
    if (this.showDiff && this.sliceCodes[this.currentSliceIndex] && this.sourceCodes[this.currentSliceIndex]) {
      this.sliceCodeDiffModel = { language: 'java', code: this.sliceCodes[this.currentSliceIndex] };
      this.sourceFileDiffModel = { language: 'java', code: this.sourceCodes[this.currentSliceIndex] };
      this.isCodeLoadingOrPrecessing = true;
      this.updateDiff();
    }
  }
}
