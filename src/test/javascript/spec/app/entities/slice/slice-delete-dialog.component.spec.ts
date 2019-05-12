/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AndroidSlicerTestModule } from '../../../test.module';
import { SliceDeleteDialogComponent } from 'app/entities/slice/slice-delete-dialog.component';
import { SliceService } from 'app/entities/slice/slice.service';

describe('Component Tests', () => {
  describe('Slice Management Delete Component', () => {
    let comp: SliceDeleteDialogComponent;
    let fixture: ComponentFixture<SliceDeleteDialogComponent>;
    let service: SliceService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AndroidSlicerTestModule],
        declarations: [SliceDeleteDialogComponent]
      })
        .overrideTemplate(SliceDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SliceDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SliceService);
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
