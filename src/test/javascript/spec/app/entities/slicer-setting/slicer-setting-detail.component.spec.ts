/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AndroidSlicerTestModule } from '../../../test.module';
import { SlicerSettingDetailComponent } from 'app/entities/slicer-setting/slicer-setting-detail.component';
import { SlicerSetting } from 'app/shared/model/slicer-setting.model';

describe('Component Tests', () => {
  describe('SlicerSetting Management Detail Component', () => {
    let comp: SlicerSettingDetailComponent;
    let fixture: ComponentFixture<SlicerSettingDetailComponent>;
    const route = ({ data: of({ slicerSetting: new SlicerSetting('123') }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AndroidSlicerTestModule],
        declarations: [SlicerSettingDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(SlicerSettingDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SlicerSettingDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.slicerSetting).toEqual(jasmine.objectContaining({ id: '123' }));
      });
    });
  });
});
