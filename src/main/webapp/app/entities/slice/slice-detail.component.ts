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

@Component({
  selector: 'jhi-slice-detail',
  templateUrl: './slice-detail.component.html'
})
export class SliceDetailComponent implements OnInit {
  slice: ISlice;

  editorOptions = { theme: 'vs', language: 'java', renderSideBySide: false, followsCaret: true, ignoreCharChanges: true };

  sourceFile: string;

  sliceCodeModel: DiffEditorModel;
  sourceFileModel: DiffEditorModel;

  poll: boolean;

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
        this.loadSourceFile();
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
          this.loadSourceFile();
        }
      });
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  loadSourceFile() {
    if (!this.sourceFileModel) {
      // get source file for comparison
      this.androidOptionsService.getServiceSource(this.slice.androidVersion, this.slice.androidClassName).subscribe(
        (res: any) => {
          this.sourceFile = res.body;

          this.sliceCodeModel = { language: 'java', code: this.slice.slice };
          this.sourceFileModel = { language: 'java', code: this.sourceFile };
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
    }
  }

  // force a re-rendering of the div container
  reloadDiff() {
    if (this.showDiff) {
      this.sliceCodeModel = null;
      this.sourceFileModel = null;
      setTimeout(() => {
        this.sliceCodeModel = { language: 'java', code: this.slice.slice };
        this.sourceFileModel = { language: 'java', code: this.sourceFile };
      }, 10);
    }
  }
}
