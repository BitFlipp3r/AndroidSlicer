import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AndroidSlicerTestModule } from '../../../test.module';
import { SlicerSettingDeleteDialogComponent } from 'app/entities/slicer-setting/slicer-setting-delete-dialog.component';
import { SlicerSettingService } from 'app/entities/slicer-setting/slicer-setting.service';

describe('Component Tests', () => {
  describe('SlicerSetting Management Delete Component', () => {
    let comp: SlicerSettingDeleteDialogComponent;
    let fixture: ComponentFixture<SlicerSettingDeleteDialogComponent>;
    let service: SlicerSettingService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AndroidSlicerTestModule],
        declarations: [SlicerSettingDeleteDialogComponent]
      })
        .overrideTemplate(SlicerSettingDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SlicerSettingDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SlicerSettingService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete('123');
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith('123');
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
