import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PaisComponent } from '../list/pais.component';
import { PaisDetailComponent } from '../detail/pais-detail.component';
import { PaisUpdateComponent } from '../update/pais-update.component';
import { PaisRoutingResolveService } from './pais-routing-resolve.service';

const paisRoute: Routes = [
  {
    path: '',
    component: PaisComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PaisDetailComponent,
    resolve: {
      pais: PaisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PaisUpdateComponent,
    resolve: {
      pais: PaisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PaisUpdateComponent,
    resolve: {
      pais: PaisRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(paisRoute)],
  exports: [RouterModule],
})
export class PaisRoutingModule {}
