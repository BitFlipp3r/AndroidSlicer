/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { AndroidSlicerTestModule } from '../../../test.module';
import { SlicerSettingUpdateComponent } from 'app/entities/slicer-setting/slicer-setting-update.component';
import { SlicerSettingService } from 'app/entities/slicer-setting/slicer-setting.service';
import { SlicerSetting } from 'app/shared/model/slicer-setting.model';

describe('Component Tests', () => {
  describe('SlicerSetting Management Update Component', () => {
    let comp: SlicerSettingUpdateComponent;
    let fixture: ComponentFixture<SlicerSettingUpdateComponent>;
    let service: SlicerSettingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AndroidSlicerTestModule],
        declarations: [SlicerSettingUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(SlicerSettingUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SlicerSettingUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SlicerSettingService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new SlicerSetting('123');
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
