import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICFAOption } from 'app/shared/model/cfa-option.model';
import { CFAOptionService } from './cfa-option.service';

@Component({
  selector: 'jhi-cfa-option-delete-dialog',
  templateUrl: './cfa-option-delete-dialog.component.html'
})
export class CFAOptionDeleteDialogComponent {
  cFAOption: ICFAOption;

  constructor(protected cFAOptionService: CFAOptionService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: string) {
    this.cFAOptionService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'cFAOptionListModification',
        content: 'Deleted an cFAOption'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-cfa-option-delete-popup',
  template: ''
})
export class CFAOptionDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ cFAOption }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(CFAOptionDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.cFAOption = cFAOption;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/cfa-options', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/cfa-options', { outlets: { popup: null } }]);
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
