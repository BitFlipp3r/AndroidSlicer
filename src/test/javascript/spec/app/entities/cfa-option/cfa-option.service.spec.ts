/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { take, map } from 'rxjs/operators';
import { CFAOptionService } from 'app/entities/cfa-option/cfa-option.service';
import { ICFAOption, CFAOption, CFAType } from 'app/shared/model/cfa-option.model';

describe('Service Tests', () => {
  describe('CFAOption Service', () => {
    let injector: TestBed;
    let service: CFAOptionService;
    let httpMock: HttpTestingController;
    let elemDefault: ICFAOption;
    let expectedResult;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(CFAOptionService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new CFAOption('ID', CFAType.ZERO_CFA, 'AAAAAAA', false);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = Object.assign({}, elemDefault);
        service
          .find('123')
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: elemDefault });
      });

      it('should update a CFAOption', async () => {
        const returnedFromService = Object.assign(
          {
            type: 'BBBBBB',
            description: 'BBBBBB',
            isDefault: true
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should return a list of CFAOption', async () => {
        const returnedFromService = Object.assign(
          {
            type: 'BBBBBB',
            description: 'BBBBBB',
            isDefault: true
          },
          elemDefault
        );
        const expected = Object.assign({}, returnedFromService);
        service
          .query(expected)
          .pipe(
            take(1),
            map(resp => resp.body)
          )
          .subscribe(body => (expectedResult = body));
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
