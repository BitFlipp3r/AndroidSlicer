import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { ICFAOption } from 'app/shared/model/cfa-option.model';

@Component({
  selector: 'jhi-cfa-option-detail',
  templateUrl: './cfa-option-detail.component.html'
})
export class CFAOptionDetailComponent implements OnInit {
  cFAOption: ICFAOption;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ cFAOption }) => {
      this.cFAOption = cFAOption;
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
