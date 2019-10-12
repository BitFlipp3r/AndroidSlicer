import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AndroidSlicerTestModule } from '../../../test.module';
import { SliceMakeComponent } from 'app/entities/slice/slice-make.component';
import { SliceService } from 'app/entities/slice/slice.service';
import { Slice } from 'app/shared/model/slice.model';
import { CFAType } from 'app/shared/model/enumerations/cfa-type.model';
import { ReflectionOptions } from 'app/shared/model/enumerations/reflection-options.model';
import { DataDependenceOptions } from 'app/shared/model/enumerations/data-dependence-options.model';
import { ControlDependenceOptions } from 'app/shared/model/enumerations/control-dependence-options.model';

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

        comp.createForm.get(['androidVersion']).patchValue('8');
        comp.createForm.get(['androidClassName']).patchValue('Lcom/android/server/AlarmManagerService');
        comp.createForm.get(['entryMethods']).patchValue(['set', 'setTime']);
        comp.createForm.get(['seedStatements']).patchValue(['checkPermission', 'enforcePermisson']);
        comp.createForm.get(['cfaOptions']).patchValue(CFAType.ZERO_CONTAINER_CFA);
        comp.createForm.get(['cfaLevel']).patchValue(null);
        comp.createForm.get(['reflectionOptions']).patchValue(ReflectionOptions.FULL);
        comp.createForm.get(['dataDependenceOptions']).patchValue(DataDependenceOptions.FULL);
        comp.createForm.get(['controlDependenceOptions']).patchValue(ControlDependenceOptions.FULL);
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
