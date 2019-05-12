import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { ISlice } from 'app/shared/model/slice.model';

@Component({
  selector: 'jhi-slice-detail',
  templateUrl: './slice-detail.component.html'
})
export class SliceDetailComponent implements OnInit {
  slice: ISlice;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ slice }) => {
      this.slice = slice;
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }
  previousState() {
    window.history.back();
  }
}
