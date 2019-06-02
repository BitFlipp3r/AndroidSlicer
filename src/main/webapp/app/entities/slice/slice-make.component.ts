import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { ISlice, Slice, ReflectionOptions, DataDependenceOptions, ControlDependenceOptions } from 'app/shared/model/slice.model';
import { SliceService } from './slice.service';
import { ISlicerOption, SlicerOptionType } from 'app/shared/model/slicer-option.model';
import { SlicerOptionService } from 'app/entities/slicer-option';
import { IAndroidVersion } from 'app/shared/model/android-version.model';
import { IAndroidClass, AndroidClass } from 'app/shared/model/android-class.model';
import { AndroidOptionsService } from 'app/shared/services/android-options.service';
import { MonacoFile } from 'ngx-monaco';
import { SelectItem } from 'primeng/components/common/selectitem';
import { constants } from 'perf_hooks';

@Component({
  selector: 'jhi-slice-make',
  templateUrl: './slice-make.component.html'
})
export class SliceMakeComponent implements OnInit {
  slice: ISlice;
  isSaving: boolean;

  versionOptions: IAndroidVersion[];

  classOptions: IAndroidClass[];

  entryMethodOptions: string[] = [];
  filteredEntryMethodOptions: string[] = [];

  seedStatementOptions: string[];
  filteredSeedStatementOptions: string[] = [];

  reflectionOptionsList: SelectItem[] = [];
  dataDependenceOptionsList: SelectItem[] = [];
  controlDependenceOptionsList: SelectItem[] = [];

  theme = 'vs';
  sourceFile: MonacoFile;

  createForm = this.fb.group({
    androidVersion: [null, [Validators.required]],
    androidClassName: [null, [Validators.required]],
    entryMethods: [null, [Validators.required]],
    seedStatements: [null, [Validators.required]],
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
    protected androidOptionsService: AndroidOptionsService,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.slice = new Slice();
    this.updateForm(this.slice);

    this.androidOptionsService.getAndroidVersions().subscribe(
      (res: HttpResponse<IAndroidVersion[]>) => {
        this.versionOptions = res.body;
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );
    this.androidOptionsService.getSeedStatements().subscribe(
      (res: HttpResponse<string[]>) => {
        this.seedStatementOptions = res.body;
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );

    this.slicerOptionService.query().subscribe(
      (res: HttpResponse<ISlicerOption[]>) => {
        for (const slicerOption of res.body) {
          const slicerOptionItem: SelectItem = { label: slicerOption.key, value: slicerOption };

          switch (slicerOption.type) {
            case SlicerOptionType.REFLECTION_OPTION: {
              this.reflectionOptionsList.push(slicerOptionItem);
              if (slicerOption.isDefault) {
                this.createForm.get(['reflectionOptions']).setValue(slicerOption);
              }
              break;
            }
            case SlicerOptionType.DATA_DEPENDENCE_OPTION: {
              this.dataDependenceOptionsList.push(slicerOptionItem);
              if (slicerOption.isDefault) {
                this.createForm.get(['dataDependenceOptions']).setValue(slicerOption);
              }
              break;
            }
            case SlicerOptionType.CONTROL_DEPENDENCE_OPTION: {
              this.controlDependenceOptionsList.push(slicerOptionItem);
              if (slicerOption.isDefault) {
                this.createForm.get(['controlDependenceOptions']).setValue(slicerOption);
              }
              break;
            }
          }
        }
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );
  }

  updateForm(slice: ISlice) {
    this.createForm.patchValue({
      androidVersion: null,
      androidClassName: null,
      entryMethods: slice.entryMethods,
      seedStatements: slice.seedStatements,
      reflectionOptions: slice.reflectionOptions,
      dataDependenceOptions: slice.dataDependenceOptions,
      controlDependenceOptions: slice.controlDependenceOptions
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const slice = this.createFromForm();
    this.subscribeToSaveResponse(this.sliceService.create(slice));
  }

  private createFromForm(): ISlice {
    const entity = {
      ...new Slice(),
      androidVersion: (this.createForm.get(['androidVersion']).value as IAndroidVersion).version,
      androidClassName: (this.createForm.get(['androidClassName']).value as IAndroidClass).name,
      entryMethods: this.createForm.get(['entryMethods']).value,
      seedStatements: this.createForm.get(['seedStatements']).value,
      reflectionOptions: (this.createForm.get(['reflectionOptions']).value as ISlicerOption).key as ReflectionOptions,
      dataDependenceOptions: (this.createForm.get(['dataDependenceOptions']).value as ISlicerOption).key as DataDependenceOptions,
      controlDependenceOptions: (this.createForm.get(['controlDependenceOptions']).value as ISlicerOption).key as ControlDependenceOptions
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

  onVersionSelection() {
    this.createForm.get(['androidClassName']).disable();

    this.androidOptionsService.getAndroidClasses((this.createForm.get(['androidVersion']).value as IAndroidVersion).path).subscribe(
      (res: HttpResponse<IAndroidClass[]>) => {
        this.classOptions = res.body;
        this.createForm.get(['androidClassName']).enable();
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );
  }

  onClassSelection() {
    const serviceClassName: string = (this.createForm.get(['androidClassName']).value as IAndroidClass).name;
    const sourceFilePath: string = (this.createForm.get(['androidClassName']).value as IAndroidClass).path;

    this.androidOptionsService.getServiceSource(sourceFilePath).subscribe(
      (res: any) => {
        this.sourceFile = {
          uri: (this.createForm.get(['androidClassName']).value as IAndroidClass).name,
          language: 'java',
          content: res.body
        };
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );

    this.androidOptionsService.getEntryMethods(serviceClassName, sourceFilePath).subscribe(
      (res: HttpResponse<string[]>) => {
        this.entryMethodOptions = res.body;
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );
  }

  filterEntryMethodOptions(event) {
    this.filteredEntryMethodOptions = [];
    this.filterMultiSelectOptions(event, this.entryMethodOptions, this.filteredEntryMethodOptions);
  }

  filterSeedStatementOptions(event) {
    this.filteredSeedStatementOptions = [];
    this.filterMultiSelectOptions(event, this.seedStatementOptions, this.filteredSeedStatementOptions);
  }

  private filterMultiSelectOptions(event, options, filterdOptions) {
    for (let i = 0; i < options.length; i++) {
      const option = options[i];
      if (option.toLowerCase().indexOf(event.query.toLowerCase()) > -1) {
        filterdOptions.push(option);
        console.log(filterdOptions);
        console.log(this.filteredSeedStatementOptions);
      }
    }
  }

  addEntryMethodOption(event: KeyboardEvent) {
    const selectedEntryMethodOptions = this.createForm.get(['entryMethods']).value;
    this.addMultiSelectOption(event, this.entryMethodOptions, selectedEntryMethodOptions);
    this.createForm.get(['entryMethods']).patchValue(selectedEntryMethodOptions);
  }

  addSeedStatementOption(event: KeyboardEvent) {
    const selectedSeedStatementOptions = this.createForm.get(['seedStatements']).value;
    this.addMultiSelectOption(event, this.seedStatementOptions, selectedSeedStatementOptions);
    this.createForm.get(['seedStatements']).patchValue(selectedSeedStatementOptions);
  }

  private addMultiSelectOption(event, options, selectedoptions) {
    if (event.key === 'Enter') {
      const tokenInput = event.srcElement as any;
      if (tokenInput.value) {
        // add value to available options
        if (!options.includes(tokenInput.value)) {
          options.push(tokenInput.value);
        }
        // add value to selected options
        if (!selectedoptions.includes(tokenInput.value)) {
          selectedoptions.push(tokenInput.value);
        }
        tokenInput.value = '';
      }
    }
  }
}
