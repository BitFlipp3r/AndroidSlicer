import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { SlicerOption } from 'app/shared/model/slicer-option.model';
import { SlicerOptionService } from './slicer-option.service';
import { SlicerOptionComponent } from './slicer-option.component';
import { SlicerOptionDetailComponent } from './slicer-option-detail.component';
import { SlicerOptionUpdateComponent } from './slicer-option-update.component';
import { SlicerOptionDeletePopupComponent } from './slicer-option-delete-dialog.component';
import { ISlicerOption } from 'app/shared/model/slicer-option.model';

@Injectable({ providedIn: 'root' })
export class SlicerOptionResolve implements Resolve<ISlicerOption> {
  constructor(private service: SlicerOptionService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ISlicerOption> {
    const id = route.params['id'] ? route.params['id'] : null;
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<SlicerOption>) => response.ok),
        map((slicerOption: HttpResponse<SlicerOption>) => slicerOption.body)
      );
    }
    return of(new SlicerOption());
  }
}

export const slicerOptionRoute: Routes = [
  {
    path: '',
    component: SlicerOptionComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'Slicer Options'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: SlicerOptionDetailComponent,
    resolve: {
      slicerOption: SlicerOptionResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Slicer Option'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: SlicerOptionUpdateComponent,
    resolve: {
      slicerOption: SlicerOptionResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Slicer Option'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: SlicerOptionUpdateComponent,
    resolve: {
      slicerOption: SlicerOptionResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Slicer Option'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const slicerOptionPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: SlicerOptionDeletePopupComponent,
    resolve: {
      slicerOption: SlicerOptionResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Slicer Options'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
