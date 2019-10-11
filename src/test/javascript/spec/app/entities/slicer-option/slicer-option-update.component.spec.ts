import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AndroidSlicerTestModule } from '../../../test.module';
import { SlicerOptionUpdateComponent } from 'app/entities/slicer-option/slicer-option-update.component';
import { SlicerOptionService } from 'app/entities/slicer-option/slicer-option.service';
import { SlicerOption } from 'app/shared/model/slicer-option.model';

describe('Component Tests', () => {
  describe('SlicerOption Management Update Component', () => {
    let comp: SlicerOptionUpdateComponent;
    let fixture: ComponentFixture<SlicerOptionUpdateComponent>;
    let service: SlicerOptionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AndroidSlicerTestModule],
        declarations: [SlicerOptionUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(SlicerOptionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SlicerOptionUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SlicerOptionService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new SlicerOption('123');
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
