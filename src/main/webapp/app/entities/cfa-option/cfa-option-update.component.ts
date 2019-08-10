import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { ICFAOption, CFAOption } from 'app/shared/model/cfa-option.model';
import { CFAOptionService } from './cfa-option.service';

@Component({
  selector: 'jhi-cfa-option-update',
  templateUrl: './cfa-option-update.component.html'
})
export class CFAOptionUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    type: [null, [Validators.required]],
    key: [null, [Validators.required]],
    description: [],
    cfaLevel: [],
    isDefault: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected cFAOptionService: CFAOptionService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ cFAOption }) => {
      this.updateForm(cFAOption);
    });
  }

  updateForm(cFAOption: ICFAOption) {
    this.editForm.patchValue({
      id: cFAOption.id,
      type: cFAOption.type,
      key: cFAOption.key,
      description: cFAOption.description,
      cfaLevel: cFAOption.cfaLevel,
      isDefault: cFAOption.isDefault
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
        const file = event.target.files[0];
        if (isImage && !/^image\//.test(file.type)) {
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
      () => console.log('blob added'), // sucess
      this.onError
    );
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const cFAOption = this.createFromForm();
    if (cFAOption.id !== undefined) {
      this.subscribeToSaveResponse(this.cFAOptionService.update(cFAOption));
    } else {
      this.subscribeToSaveResponse(this.cFAOptionService.create(cFAOption));
    }
  }

  private createFromForm(): ICFAOption {
    return {
      ...new CFAOption(),
      id: this.editForm.get(['id']).value,
      type: this.editForm.get(['type']).value,
      key: this.editForm.get(['key']).value,
      description: this.editForm.get(['description']).value,
      cfaLevel: this.editForm.get(['cfaLevel']).value,
      isDefault: this.editForm.get(['isDefault']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICFAOption>>) {
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
