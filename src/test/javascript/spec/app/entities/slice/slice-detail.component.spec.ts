/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AndroidSlicerTestModule } from '../../../test.module';
import { SliceDetailComponent } from 'app/entities/slice/slice-detail.component';
import { Slice } from 'app/shared/model/slice.model';

describe('Component Tests', () => {
  describe('Slice Management Detail Component', () => {
    let comp: SliceDetailComponent;
    let fixture: ComponentFixture<SliceDetailComponent>;
    const route = ({ data: of({ slice: new Slice('123') }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AndroidSlicerTestModule],
        declarations: [SliceDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(SliceDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SliceDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.slice).toEqual(jasmine.objectContaining({ id: '123' }));
      });
    });
  });
});
