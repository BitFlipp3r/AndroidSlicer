/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AndroidSlicerTestModule } from '../../../test.module';
import { CFAOptionDetailComponent } from 'app/entities/cfa-option/cfa-option-detail.component';
import { CFAOption } from 'app/shared/model/cfa-option.model';

describe('Component Tests', () => {
  describe('CFAOption Management Detail Component', () => {
    let comp: CFAOptionDetailComponent;
    let fixture: ComponentFixture<CFAOptionDetailComponent>;
    const route = ({ data: of({ cFAOption: new CFAOption('123') }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AndroidSlicerTestModule],
        declarations: [CFAOptionDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(CFAOptionDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CFAOptionDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.cFAOption).toEqual(jasmine.objectContaining({ id: '123' }));
      });
    });
  });
});
