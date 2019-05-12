import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISlicerSetting } from 'app/shared/model/slicer-setting.model';

@Component({
  selector: 'jhi-slicer-setting-detail',
  templateUrl: './slicer-setting-detail.component.html'
})
export class SlicerSettingDetailComponent implements OnInit {
  slicerSetting: ISlicerSetting;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ slicerSetting }) => {
      this.slicerSetting = slicerSetting;
    });
  }

  previousState() {
    window.history.back();
  }
}
