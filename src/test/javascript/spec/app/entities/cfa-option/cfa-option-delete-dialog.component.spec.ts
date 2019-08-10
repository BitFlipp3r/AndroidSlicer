/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AndroidSlicerTestModule } from '../../../test.module';
import { CFAOptionDeleteDialogComponent } from 'app/entities/cfa-option/cfa-option-delete-dialog.component';
import { CFAOptionService } from 'app/entities/cfa-option/cfa-option.service';

describe('Component Tests', () => {
  describe('CFAOption Management Delete Component', () => {
    let comp: CFAOptionDeleteDialogComponent;
    let fixture: ComponentFixture<CFAOptionDeleteDialogComponent>;
    let service: CFAOptionService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AndroidSlicerTestModule],
        declarations: [CFAOptionDeleteDialogComponent]
      })
        .overrideTemplate(CFAOptionDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CFAOptionDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(CFAOptionService);
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
