/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AndroidSlicerTestModule } from '../../../test.module';
import { SlicerOptionDetailComponent } from 'app/entities/slicer-option/slicer-option-detail.component';
import { SlicerOption } from 'app/shared/model/slicer-option.model';

describe('Component Tests', () => {
  describe('SlicerOption Management Detail Component', () => {
    let comp: SlicerOptionDetailComponent;
    let fixture: ComponentFixture<SlicerOptionDetailComponent>;
    const route = ({ data: of({ slicerOption: new SlicerOption('123') }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AndroidSlicerTestModule],
        declarations: [SlicerOptionDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(SlicerOptionDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SlicerOptionDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.slicerOption).toEqual(jasmine.objectContaining({ id: '123' }));
      });
    });
  });
});
