import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { ISlicerOption } from 'app/shared/model/slicer-option.model';

@Component({
  selector: 'jhi-slicer-option-detail',
  templateUrl: './slicer-option-detail.component.html'
})
export class SlicerOptionDetailComponent implements OnInit {
  slicerOption: ISlicerOption;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ slicerOption }) => {
      this.slicerOption = slicerOption;
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
