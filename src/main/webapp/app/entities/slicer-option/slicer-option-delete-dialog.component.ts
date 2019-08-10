import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISlicerOption } from 'app/shared/model/slicer-option.model';
import { SlicerOptionService } from './slicer-option.service';

@Component({
  selector: 'jhi-slicer-option-delete-dialog',
  templateUrl: './slicer-option-delete-dialog.component.html'
})
export class SlicerOptionDeleteDialogComponent {
  slicerOption: ISlicerOption;

  constructor(
    protected slicerOptionService: SlicerOptionService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: string) {
    this.slicerOptionService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'slicerOptionListModification',
        content: 'Deleted an slicerOption'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-slicer-option-delete-popup',
  template: ''
})
export class SlicerOptionDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ slicerOption }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(SlicerOptionDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.slicerOption = slicerOption;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/slicer-options', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/slicer-options', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
