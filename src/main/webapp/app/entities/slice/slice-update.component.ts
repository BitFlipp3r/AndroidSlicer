import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { ISlice, Slice } from 'app/shared/model/slice.model';
import { SliceService } from './slice.service';
import { ISlicerOption } from 'app/shared/model/slicer-option.model';
import { SlicerOptionService } from 'app/entities/slicer-option';

@Component({
  selector: 'jhi-slice-update',
  templateUrl: './slice-update.component.html'
})
export class SliceUpdateComponent implements OnInit {
  slice: ISlice;
  isSaving: boolean;

  reflectionoptions: ISlicerOption[];

  datadependenceoptions: ISlicerOption[];

  controldependenceoptions: ISlicerOption[];

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
    reflectionOptions: [null, Validators.required],
    dataDependenceOptions: [null, Validators.required],
    controlDependenceOptions: [null, Validators.required]
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected sliceService: SliceService,
    protected slicerOptionService: SlicerOptionService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ slice }) => {
      this.updateForm(slice);
      this.slice = slice;
    });
    this.slicerOptionService
      .query({ filter: 'slice-is-null' })
      .pipe(
        filter((mayBeOk: HttpResponse<ISlicerOption[]>) => mayBeOk.ok),
        map((response: HttpResponse<ISlicerOption[]>) => response.body)
      )
      .subscribe(
        (res: ISlicerOption[]) => {
          if (!this.slice.reflectionOptions || !this.slice.reflectionOptions.id) {
            this.reflectionoptions = res;
          } else {
            this.slicerOptionService
              .find(this.slice.reflectionOptions.id)
              .pipe(
                filter((subResMayBeOk: HttpResponse<ISlicerOption>) => subResMayBeOk.ok),
                map((subResponse: HttpResponse<ISlicerOption>) => subResponse.body)
              )
              .subscribe(
                (subRes: ISlicerOption) => (this.reflectionoptions = [subRes].concat(res)),
                (subRes: HttpErrorResponse) => this.onError(subRes.message)
              );
          }
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
    this.slicerOptionService
      .query({ filter: 'slice-is-null' })
      .pipe(
        filter((mayBeOk: HttpResponse<ISlicerOption[]>) => mayBeOk.ok),
        map((response: HttpResponse<ISlicerOption[]>) => response.body)
      )
      .subscribe(
        (res: ISlicerOption[]) => {
          if (!this.slice.dataDependenceOptions || !this.slice.dataDependenceOptions.id) {
            this.datadependenceoptions = res;
          } else {
            this.slicerOptionService
              .find(this.slice.dataDependenceOptions.id)
              .pipe(
                filter((subResMayBeOk: HttpResponse<ISlicerOption>) => subResMayBeOk.ok),
                map((subResponse: HttpResponse<ISlicerOption>) => subResponse.body)
              )
              .subscribe(
                (subRes: ISlicerOption) => (this.datadependenceoptions = [subRes].concat(res)),
                (subRes: HttpErrorResponse) => this.onError(subRes.message)
              );
          }
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
    this.slicerOptionService
      .query({ filter: 'slice-is-null' })
      .pipe(
        filter((mayBeOk: HttpResponse<ISlicerOption[]>) => mayBeOk.ok),
        map((response: HttpResponse<ISlicerOption[]>) => response.body)
      )
      .subscribe(
        (res: ISlicerOption[]) => {
          if (!this.slice.controlDependenceOptions || !this.slice.controlDependenceOptions.id) {
            this.controldependenceoptions = res;
          } else {
            this.slicerOptionService
              .find(this.slice.controlDependenceOptions.id)
              .pipe(
                filter((subResMayBeOk: HttpResponse<ISlicerOption>) => subResMayBeOk.ok),
                map((subResponse: HttpResponse<ISlicerOption>) => subResponse.body)
              )
              .subscribe(
                (subRes: ISlicerOption) => (this.controldependenceoptions = [subRes].concat(res)),
                (subRes: HttpErrorResponse) => this.onError(subRes.message)
              );
          }
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
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
    const slice = this.createFromForm();
    if (slice.id !== undefined) {
      this.subscribeToSaveResponse(this.sliceService.update(slice));
    } else {
      this.subscribeToSaveResponse(this.sliceService.create(slice));
    }
  }

  private createFromForm(): ISlice {
    const entity = {
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
    return entity;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISlice>>) {
    result.subscribe((res: HttpResponse<ISlice>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

  trackSlicerOptionById(index: number, item: ISlicerOption) {
    return item.id;
  }
}
