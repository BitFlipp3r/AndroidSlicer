import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
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
    description: [],
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
      description: cFAOption.description,
      isDefault: cFAOption.isDefault
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const cFAOption = this.createFromForm();
    if (cFAOption.id !== undefined) {
      this.subscribeToSaveResponse(this.cFAOptionService.update(cFAOption));
    }
  }

  private createFromForm(): ICFAOption {
    return {
      ...new CFAOption(),
      id: this.editForm.get(['id']).value,
      type: this.editForm.get(['type']).value,
      description: this.editForm.get(['description']).value,
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
