import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { ISlicerSetting, SlicerSetting } from 'app/shared/model/slicer-setting.model';
import { SlicerSettingService } from './slicer-setting.service';

@Component({
  selector: 'jhi-slicer-setting-update',
  templateUrl: './slicer-setting-update.component.html'
})
export class SlicerSettingUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    key: [null, [Validators.required]],
    value: [null, [Validators.required]],
    description: []
  });

  constructor(protected slicerSettingService: SlicerSettingService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ slicerSetting }) => {
      this.updateForm(slicerSetting);
    });
  }

  updateForm(slicerSetting: ISlicerSetting) {
    this.editForm.patchValue({
      id: slicerSetting.id,
      key: slicerSetting.key,
      value: slicerSetting.value,
      description: slicerSetting.description
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const slicerSetting = this.createFromForm();
    if (slicerSetting.id !== undefined) {
      this.subscribeToSaveResponse(this.slicerSettingService.update(slicerSetting));
    }
  }

  private createFromForm(): ISlicerSetting {
    return {
      ...new SlicerSetting(),
      id: this.editForm.get(['id']).value,
      key: this.editForm.get(['key']).value,
      value: this.editForm.get(['value']).value,
      description: this.editForm.get(['description']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISlicerSetting>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}
