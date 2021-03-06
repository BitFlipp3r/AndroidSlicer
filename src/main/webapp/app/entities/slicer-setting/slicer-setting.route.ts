import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { SlicerSetting } from 'app/shared/model/slicer-setting.model';
import { SlicerSettingService } from './slicer-setting.service';
import { SlicerSettingComponent } from './slicer-setting.component';
import { SlicerSettingDetailComponent } from './slicer-setting-detail.component';
import { SlicerSettingUpdateComponent } from './slicer-setting-update.component';
import { ISlicerSetting } from 'app/shared/model/slicer-setting.model';

@Injectable({ providedIn: 'root' })
export class SlicerSettingResolve implements Resolve<ISlicerSetting> {
  constructor(private service: SlicerSettingService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ISlicerSetting> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<SlicerSetting>) => response.ok),
        map((slicerSetting: HttpResponse<SlicerSetting>) => slicerSetting.body)
      );
    }
    return of(new SlicerSetting());
  }
}

export const slicerSettingRoute: Routes = [
  {
    path: '',
    component: SlicerSettingComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      // authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'Slicer Settings'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: SlicerSettingDetailComponent,
    resolve: {
      slicerSetting: SlicerSettingResolve
    },
    data: {
      // authorities: ['ROLE_USER'],
      pageTitle: 'Slicer Setting'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: SlicerSettingUpdateComponent,
    resolve: {
      slicerSetting: SlicerSettingResolve
    },
    data: {
      // authorities: ['ROLE_USER'],
      pageTitle: 'Slicer Setting'
    },
    canActivate: [UserRouteAccessService]
  }
];
