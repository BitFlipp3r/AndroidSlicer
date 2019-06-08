import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { ISlice } from 'app/shared/model/slice.model';
import { interval } from 'rxjs';
import { startWith, switchMap, takeWhile } from 'rxjs/operators';
import { MonacoFile } from 'ngx-monaco';
import { SliceService } from '.';

@Component({
  selector: 'jhi-slice-detail',
  templateUrl: './slice-detail.component.html'
})
export class SliceDetailComponent implements OnInit {
  slice: ISlice;

  theme = 'vs-light';
  code: MonacoFile;

  poll: boolean;

  constructor(
    protected dataUtils: JhiDataUtils,
    protected activatedRoute: ActivatedRoute,
    private sliceService: SliceService,
    private router: Router
  ) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ slice }) => {
      this.slice = slice;
      this.code = { uri: slice.androidClassName, language: 'java', content: slice.slice };
      if (slice.running) {
        // update until slicing has finished
        this.refresh();
        this.poll = true;
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
        if (!this.slice.running) {
          this.code = { uri: this.slice.androidClassName, language: 'java', content: this.slice.slice };
          // scroll log to bottom
          const logTxt = document.getElementById('logTxt');
          logTxt.scrollTop = logTxt.scrollHeight;
        }
      });
  }
}
