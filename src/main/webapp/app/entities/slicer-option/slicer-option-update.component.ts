import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { ISlicerOption, SlicerOption } from 'app/shared/model/slicer-option.model';
import { SlicerOptionService } from './slicer-option.service';

@Component({
  selector: 'jhi-slicer-option-update',
  templateUrl: './slicer-option-update.component.html'
})
export class SlicerOptionUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    type: [null, [Validators.required]],
    key: [null, [Validators.required]],
    description: [],
    isDefault: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected slicerOptionService: SlicerOptionService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ slicerOption }) => {
      this.updateForm(slicerOption);
    });
  }

  updateForm(slicerOption: ISlicerOption) {
    this.editForm.patchValue({
      id: slicerOption.id,
      type: slicerOption.type,
      key: slicerOption.key,
      description: slicerOption.description,
      isDefault: slicerOption.isDefault
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  setFileData(event, field: string, isImage) {
    return new Promise((resolve, reject) => {
      if (event && event.target && event.target.files && event.target.files[0]) {
        const file: File = event.target.files[0];
        if (isImage && !file.type.startsWith('image/')) {
          reject(`File was expected to be an image but was found to be ${file.type}`);
        } else {
          const filedContentType: string = field + 'ContentType';
          this.dataUtils.toBase64(file, base64Data => {
            this.editForm.patchValue({
              [field]: base64Data,
              [filedContentType]: file.type
            });
          });
        }
      } else {
        reject(`Base64 data was not set as file could not be extracted from passed parameter: ${event}`);
      }
    }).then(
      // eslint-disable-next-line no-console
      () => console.log('blob added'), // success
      this.onError
    );
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const slicerOption = this.createFromForm();
    if (slicerOption.id !== undefined) {
      this.subscribeToSaveResponse(this.slicerOptionService.update(slicerOption));
    } else {
      this.subscribeToSaveResponse(this.slicerOptionService.create(slicerOption));
    }
  }

  private createFromForm(): ISlicerOption {
    return {
      ...new SlicerOption(),
      id: this.editForm.get(['id']).value,
      type: this.editForm.get(['type']).value,
      key: this.editForm.get(['key']).value,
      description: this.editForm.get(['description']).value,
      isDefault: this.editForm.get(['isDefault']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISlicerOption>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
