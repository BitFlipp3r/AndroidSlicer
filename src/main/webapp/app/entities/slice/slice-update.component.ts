import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { ISlice, Slice } from 'app/shared/model/slice.model';
import { SliceService } from './slice.service';

@Component({
  selector: 'jhi-slice-update',
  templateUrl: './slice-update.component.html'
})
export class SliceUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    androidVersion: [null, [Validators.required]],
    androidClassName: [null, [Validators.required]],
    entryMethods: [null, [Validators.required]],
    seedStatements: [null, [Validators.required]],
    slice: [],
    log: [],
    threadId: [],
    running: [],
    reflectionOptions: [null, [Validators.required]],
    dataDependenceOptions: [null, [Validators.required]],
    controlDependenceOptions: [null, [Validators.required]]
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected sliceService: SliceService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ slice }) => {
      this.updateForm(slice);
    });
  }

  updateForm(slice: ISlice) {
    this.editForm.patchValue({
      id: slice.id,
      androidVersion: slice.androidVersion,
      androidClassName: slice.androidClassName,
      entryMethods: slice.entryMethods,
      seedStatements: slice.seedStatements,
      slice: slice.slice,
      log: slice.log,
      threadId: slice.threadId,
      running: slice.running,
      reflectionOptions: slice.reflectionOptions,
      dataDependenceOptions: slice.dataDependenceOptions,
      controlDependenceOptions: slice.controlDependenceOptions
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
    const slice = this.createFromForm();
    if (slice.id !== undefined) {
      this.subscribeToSaveResponse(this.sliceService.update(slice));
    } else {
      this.subscribeToSaveResponse(this.sliceService.create(slice));
    }
  }

  private createFromForm(): ISlice {
    return {
      ...new Slice(),
      id: this.editForm.get(['id']).value,
      androidVersion: this.editForm.get(['androidVersion']).value,
      androidClassName: this.editForm.get(['androidClassName']).value,
      entryMethods: this.editForm.get(['entryMethods']).value,
      seedStatements: this.editForm.get(['seedStatements']).value,
      slice: this.editForm.get(['slice']).value,
      log: this.editForm.get(['log']).value,
      threadId: this.editForm.get(['threadId']).value,
      running: this.editForm.get(['running']).value,
      reflectionOptions: this.editForm.get(['reflectionOptions']).value,
      dataDependenceOptions: this.editForm.get(['dataDependenceOptions']).value,
      controlDependenceOptions: this.editForm.get(['controlDependenceOptions']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISlice>>) {
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
