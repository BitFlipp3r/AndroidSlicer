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
import { SelectItem } from 'primeng/components/common/selectitem';
import { ICFAOption, CFAType } from 'app/shared/model/cfa-option.model';
import { CFAOptionService } from 'app/entities/cfa-option';

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

  cfaOptionsList: SelectItem[] = [];
  cfaLevelNeeded = false;
  reflectionOptionsList: SelectItem[] = [];
  dataDependenceOptionsList: SelectItem[] = [];
  controlDependenceOptionsList: SelectItem[] = [];

  editorOptions = { theme: 'vs', language: 'java' };
  sourceFile: String;

  createForm = this.fb.group({
    androidVersion: [null, [Validators.required]],
    androidClassName: [null, [Validators.required]],
    entryMethods: [null, [Validators.required]],
    seedStatements: [null, [Validators.required]],
    cfaOptions: [null, [Validators.required]],
    cfaLevel: [null],
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
    protected cfaOptionService: CFAOptionService,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.slice = new Slice();

    this.androidOptionsService.getAndroidVersions().subscribe(
      (res: HttpResponse<IAndroidVersion[]>) => {
        this.versionOptions = res.body;

        // preselect if only one
        if (this.versionOptions && this.versionOptions.length === 1) {
          this.createForm.get(['androidVersion']).patchValue(this.versionOptions[0]);
          this.onVersionSelection();
        }
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );
    this.androidOptionsService.getSeedStatements().subscribe(
      (res: HttpResponse<string[]>) => {
        this.seedStatementOptions = res.body;
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );

    this.cfaOptionService.getAll().subscribe(
      (res: HttpResponse<ICFAOption[]>) => {
        for (const cfaOption of res.body) {
          const cfaOptionItem: SelectItem = { label: cfaOption.type, value: cfaOption };
          this.cfaOptionsList.push(cfaOptionItem);

          if (cfaOption.isDefault) {
            this.createForm.get(['cfaOptions']).setValue(cfaOption);
          }
        }
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );

    this.slicerOptionService.getAll().subscribe(
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
      cfaType: (this.createForm.get(['cfaOptions']).value as ICFAOption).type,
      cfaLevel: this.createForm.get(['cfaLevel']).value,
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

    this.androidOptionsService
      .getAndroidClasses((this.createForm.get(['androidVersion']).value as IAndroidVersion).path)
      .subscribe(
        // ok
        (res: HttpResponse<IAndroidClass[]>) => {
          this.classOptions = res.body;
        },
        // error
        (res: HttpErrorResponse) => this.onError(res.message)
      )
      .add(() => {
        // finally
        this.createForm.get(['androidClassName']).enable();
      });
  }

  onClassSelection() {
    const androidVersion: number = (this.createForm.get(['androidVersion']).value as IAndroidVersion).version;
    const serviceClassName: string = (this.createForm.get(['androidClassName']).value as IAndroidClass).name;
    const sourceFilePath: string = (this.createForm.get(['androidClassName']).value as IAndroidClass).path;

    this.createForm.get(['entryMethods']).disable();

    this.androidOptionsService.getServiceSource(androidVersion, serviceClassName).subscribe(
      (res: any) => {
        this.sourceFile = res.body;
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );

    this.androidOptionsService
      .getEntryMethods(serviceClassName, sourceFilePath)
      .subscribe(
        // ok
        (res: HttpResponse<string[]>) => {
          this.entryMethodOptions = res.body;
        },
        // error
        (res: HttpErrorResponse) => {
          this.entryMethodOptions = [];
          this.onError(res.message);
        }
      )
      .add(() => {
        // finally
        this.createForm.get(['entryMethods']).enable();
      });
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
      if (
        option
          .toString()
          .toLowerCase()
          .indexOf(event.query.toString().toLowerCase()) > -1
      ) {
        filterdOptions.push(option);
      }
    }
  }

  addEntryMethodOption(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      const selectedEntryMethodOptions = this.createForm.get(['entryMethods']).value || [];
      this.addMultiSelectOption(event, this.entryMethodOptions, selectedEntryMethodOptions);
      this.createForm.get(['entryMethods']).patchValue(selectedEntryMethodOptions);
    }
  }

  addSeedStatementOption(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      const selectedSeedStatementOptions = this.createForm.get(['seedStatements']).value || [];
      this.addMultiSelectOption(event, this.seedStatementOptions, selectedSeedStatementOptions);
      this.createForm.get(['seedStatements']).patchValue(selectedSeedStatementOptions);
    }
  }

  private addMultiSelectOption(event, options, selectedOptions) {
    if (event.key === 'Enter') {
      const tokenInput = event.srcElement || (event.target as any);
      if (tokenInput.value) {
        // add value to available options
        if (!options.includes(tokenInput.value)) {
          options.push(tokenInput.value);
        }
        // add value to selected options
        if (!selectedOptions.includes(tokenInput.value)) {
          selectedOptions.push(tokenInput.value);
        }
        tokenInput.value = '';
      }
    }
  }

  addAllEntryMethods() {
    this.createForm.get(['entryMethods']).patchValue(this.entryMethodOptions);
  }

  clearEntryMethods() {
    this.createForm.get(['entryMethods']).patchValue([]);
  }

  addAllSeedStatements() {
    this.createForm.get(['seedStatements']).patchValue(this.seedStatementOptions);
  }

  clearSeedStatements() {
    this.createForm.get(['seedStatements']).patchValue([]);
  }

  onCfaOptionSelection() {
    const selectedCfaType = (this.createForm.get(['cfaOptions']).value as ICFAOption).type;
    if (selectedCfaType === CFAType.N_CFA || selectedCfaType === CFAType.VANILLA_N_CFA) {
      this.cfaLevelNeeded = true;
      this.createForm.get(['cfaLevel']).setValidators([Validators.required]);
    } else {
      this.createForm.get(['cfaLevel']).patchValue(null);
      this.createForm.get(['cfaLevel']).setValidators(null);
      this.cfaLevelNeeded = false;
    }
    this.createForm.get(['cfaLevel']).updateValueAndValidity();
  }
}
