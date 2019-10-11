import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AndroidSlicerTestModule } from '../../../test.module';
import { SliceUpdateComponent } from 'app/entities/slice/slice-update.component';
import { SliceService } from 'app/entities/slice/slice.service';
import { Slice } from 'app/shared/model/slice.model';

describe('Component Tests', () => {
  describe('Slice Management Update Component', () => {
    let comp: SliceUpdateComponent;
    let fixture: ComponentFixture<SliceUpdateComponent>;
    let service: SliceService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AndroidSlicerTestModule],
        declarations: [SliceUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(SliceUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SliceUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SliceService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Slice('123');
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Slice();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
