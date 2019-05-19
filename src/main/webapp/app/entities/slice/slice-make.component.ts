import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { ISlice, Slice } from 'app/shared/model/slice.model';
import { SliceService } from './slice.service';
import { ISlicerOption, SlicerOptionType } from 'app/shared/model/slicer-option.model';
import { SlicerOptionService } from 'app/entities/slicer-option';
import { IAndroidVersion } from 'app/shared/model/android-version.model';
import { IAndroidClass, AndroidClass } from 'app/shared/model/android-class.model';
import { AndroidOptionsService } from 'app/shared/services/android-options.service';
import { Dropdown } from 'primeng/dropdown';
import { MonacoFile } from 'ngx-monaco';
import { SelectItem } from 'primeng/components/common/selectitem';

@Component({
  selector: 'jhi-slice-make',
  templateUrl: './slice-make.component.html'
})
export class SliceMakeComponent implements OnInit {
  slice: ISlice;
  isSaving: boolean;

  reflectionoptions: ISlicerOption[];

  datadependenceoptions: ISlicerOption[];

  controldependenceoptions: ISlicerOption[];

  versionOptions: IAndroidVersion[];

  classOptions: IAndroidClass[];

  entryMethodOptions: string[];
  filteredEntryMethodOptions: string[] = [];

  seedStatementOptions: string[];
  filteredSeedStatementOptions: string[] = [];

  reflectionOptionList: SelectItem[] = [];
  dataDependenceOptionList: SelectItem[] = [];
  controlDependenceOptionList: SelectItem[] = [];

  theme = 'vs';
  sourceFile: MonacoFile;

  createForm = this.fb.group({
    androidVersion: [null, [Validators.required]],
    androidClassName: [null, [Validators.required]],
    entryMethods: [null, [Validators.required]],
    seedStatements: [null, [Validators.required]],
    reflectionOption: [null, Validators.required],
    dataDependenceOption: [null, Validators.required],
    controlDependenceOption: [null, Validators.required]
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
          //const slicerOptionItem: SelectItem = { label: slicerOption.key, value: slicerOption.description };
          switch (slicerOption.type) {
            case SlicerOptionType.REFLECTION_OPTION: {
              this.reflectionOptionList.push(slicerOption);
              this.setDefault(slicerOption, slicerOptionItem, this.createForm.get(['reflectionOption']).value);
              break;
            }
            case SlicerOptionType.DATA_DEPENDENCE_OPTION: {
              this.dataDependenceOptionList.push({ label: slicerOption.key, value: slicerOption.description });
              this.setDefault(slicerOption, slicerOptionItem, this.createForm.get(['dataDependenceOption']).value);
              break;
            }
            case SlicerOptionType.CONTROL_DEPENDENCE_OPTION: {
              this.controlDependenceOptionList.push({ label: slicerOption.key, value: slicerOption.description });
              this.setDefault(slicerOption, slicerOptionItem, this.createForm.get(['controlDependenceOption']).value);
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
      androidVersion: slice.androidVersion,
      androidClassName: slice.androidClassName,
      entryMethods: slice.entryMethods,
      seedStatements: slice.seedStatements,
      reflectionOption: slice.reflectionOption,
      dataDependenceOption: slice.dataDependenceOption,
      controlDependenceOption: slice.controlDependenceOption
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
      reflectionOption: (this.createForm.get(['reflectionOption']).value as SelectItem).label,
      dataDependenceOption: (this.createForm.get(['dataDependenceOption']).value as SelectItem).label,
      controlDependenceOption: (this.createForm.get(['controlDependenceOption']).value as SelectItem).label
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

  onVersionSelection(androidClassNameDropdown: Dropdown) {
    this.slice.androidVersion = this.selectedVersion.version;
    androidClassNameDropdown.setDisabledState(true);

    this.androidOptionsService.getAndroidClasses(this.selectedVersion.path).subscribe(
      (res: HttpResponse<IAndroidClass[]>) => {
        this.classOptions = res.body;
        androidClassNameDropdown.setDisabledState(false);
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );
  }

  onClassSelection() {
    this.androidOptionsService.getServiceSource(this.selectedClass.path).subscribe(
      (res: any) => {
        this.sourceFile = { uri: this.selectedClass.name, language: 'java', content: res.body };
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );
  }

  filterSeedStatementOptions(event, options, filterdOptions) {
    filterdOptions = [];
    for (let i = 0; i < options.length; i++) {
      const option = options[i];
      if (option.toLowerCase().indexOf(event.query.toLowerCase()) > -1) {
        filterdOptions.push(option);
      }
    }
  }

  addSeedStatementOption(event, options) {
    if (event.key === 'Enter') {
      const tokenInput = event.srcElement as any;
      if (tokenInput.value) {
        if (!options.includes(tokenInput.value)) {
          options.push(tokenInput.value);
        }
        if (!event.target.value.includes(tokenInput.value)) {
          this.createForm.get(['seedStatements']).value.push(tokenInput.value);
        }
        tokenInput.value = '';
      }
    }
  }

  private setDefault(slicerOption: ISlicerOption, slicerOptionItem: SelectItem, formControlValue: any) {
    if (slicerOption.isDefault) {
      formControlValue = slicerOptionItem;
    }
  }
}
