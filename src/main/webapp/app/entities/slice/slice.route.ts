import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Slice } from 'app/shared/model/slice.model';
import { SliceService } from './slice.service';
import { SliceComponent } from './slice.component';
import { SliceDetailComponent } from './slice-detail.component';
import { SliceMakeComponent } from './slice-make.component';
import { SliceDeletePopupComponent } from './slice-delete-dialog.component';
import { ISlice } from 'app/shared/model/slice.model';

@Injectable({ providedIn: 'root' })
export class SliceResolve implements Resolve<ISlice> {
  constructor(private service: SliceService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ISlice> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Slice>) => response.ok),
        map((slice: HttpResponse<Slice>) => slice.body)
      );
    }
    return of(new Slice());
  }
}

export const sliceRoute: Routes = [
  {
    path: '',
    component: SliceComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'Slices'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: SliceDetailComponent,
    resolve: {
      slice: SliceResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Slice'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: SliceMakeComponent,
    resolve: {
      slice: SliceResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Slice'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const slicePopupRoute: Routes = [
  {
    path: ':id/delete',
    component: SliceDeletePopupComponent,
    resolve: {
      slice: SliceResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Slice'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
