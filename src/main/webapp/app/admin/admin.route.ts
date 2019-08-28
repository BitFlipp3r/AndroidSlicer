import { Routes } from '@angular/router';

import { configurationRoute, docsRoute, healthRoute, metricsRoute } from './';

import { UserRouteAccessService } from 'app/core';

const ADMIN_ROUTES = [configurationRoute, docsRoute, healthRoute, metricsRoute];

export const adminState: Routes = [
  {
    path: '',
    data: {
      // authorities: ['ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService],
    children: ADMIN_ROUTES
  }
];
