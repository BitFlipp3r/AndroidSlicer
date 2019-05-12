import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISlice } from 'app/shared/model/slice.model';
import { SliceService } from './slice.service';

@Component({
  selector: 'jhi-slice-delete-dialog',
  templateUrl: './slice-delete-dialog.component.html'
})
export class SliceDeleteDialogComponent {
  slice: ISlice;

  constructor(protected sliceService: SliceService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: string) {
    this.sliceService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'sliceListModification',
        content: 'Deleted an slice'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-slice-delete-popup',
  template: ''
})
export class SliceDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ slice }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(SliceDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.slice = slice;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/slice', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/slice', { outlets: { popup: null } }]);
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
