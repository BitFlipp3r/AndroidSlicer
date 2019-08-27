/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { AndroidSlicerTestModule } from '../../../test.module';
import { SliceMakeComponent } from 'app/entities/slice/slice-make.component';
import { SliceService } from 'app/entities/slice/slice.service';
import { Slice } from 'app/shared/model/slice.model';

describe('Component Tests', () => {
  describe('Slice Management Make Component', () => {
    let comp: SliceMakeComponent;
    let fixture: ComponentFixture<SliceMakeComponent>;
    let service: SliceService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AndroidSlicerTestModule],
        declarations: [SliceMakeComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(SliceMakeComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SliceMakeComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SliceService);
    });

    describe('save', () => {
      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Slice();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        // WHEN
        // TODO: set needed fields
        // createForm.get(['androidVersion']).patchValue ...
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
