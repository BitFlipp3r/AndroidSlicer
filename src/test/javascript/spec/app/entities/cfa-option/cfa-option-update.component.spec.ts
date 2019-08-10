/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { AndroidSlicerTestModule } from '../../../test.module';
import { CFAOptionUpdateComponent } from 'app/entities/cfa-option/cfa-option-update.component';
import { CFAOptionService } from 'app/entities/cfa-option/cfa-option.service';
import { CFAOption } from 'app/shared/model/cfa-option.model';

describe('Component Tests', () => {
  describe('CFAOption Management Update Component', () => {
    let comp: CFAOptionUpdateComponent;
    let fixture: ComponentFixture<CFAOptionUpdateComponent>;
    let service: CFAOptionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AndroidSlicerTestModule],
        declarations: [CFAOptionUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(CFAOptionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CFAOptionUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(CFAOptionService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new CFAOption('123');
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
        const entity = new CFAOption();
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
