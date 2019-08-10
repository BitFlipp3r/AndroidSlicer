import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISlicerSetting } from 'app/shared/model/slicer-setting.model';
import { SlicerSettingService } from './slicer-setting.service';

@Component({
  selector: 'jhi-slicer-setting-delete-dialog',
  templateUrl: './slicer-setting-delete-dialog.component.html'
})
export class SlicerSettingDeleteDialogComponent {
  slicerSetting: ISlicerSetting;

  constructor(
    protected slicerSettingService: SlicerSettingService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: string) {
    this.slicerSettingService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'slicerSettingListModification',
        content: 'Deleted an slicerSetting'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-slicer-setting-delete-popup',
  template: ''
})
export class SlicerSettingDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ slicerSetting }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(SlicerSettingDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.slicerSetting = slicerSetting;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/slicer-settings', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/slicer-settings', { outlets: { popup: null } }]);
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
