import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { CFAOption } from 'app/shared/model/cfa-option.model';
import { CFAOptionService } from './cfa-option.service';
import { CFAOptionComponent } from './cfa-option.component';
import { CFAOptionDetailComponent } from './cfa-option-detail.component';
import { CFAOptionUpdateComponent } from './cfa-option-update.component';
import { ICFAOption } from 'app/shared/model/cfa-option.model';

@Injectable({ providedIn: 'root' })
export class CFAOptionResolve implements Resolve<ICFAOption> {
  constructor(private service: CFAOptionService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ICFAOption> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<CFAOption>) => response.ok),
        map((cFAOption: HttpResponse<CFAOption>) => cFAOption.body)
      );
    }
    return of(new CFAOption());
  }
}

export const cFAOptionRoute: Routes = [
  {
    path: '',
    component: CFAOptionComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'CFA Options'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: CFAOptionDetailComponent,
    resolve: {
      cFAOption: CFAOptionResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'CFA Option'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: CFAOptionUpdateComponent,
    resolve: {
      cFAOption: CFAOptionResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'CFA Option'
    },
    canActivate: [UserRouteAccessService]
  }
];
