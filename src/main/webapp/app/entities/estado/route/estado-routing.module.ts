import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EstadoComponent } from '../list/estado.component';
import { EstadoDetailComponent } from '../detail/estado-detail.component';
import { EstadoUpdateComponent } from '../update/estado-update.component';
import { EstadoRoutingResolveService } from './estado-routing-resolve.service';

const estadoRoute: Routes = [
  {
    path: '',
    component: EstadoComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EstadoDetailComponent,
    resolve: {
      estado: EstadoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EstadoUpdateComponent,
    resolve: {
      estado: EstadoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EstadoUpdateComponent,
    resolve: {
      estado: EstadoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(estadoRoute)],
  exports: [RouterModule],
})
export class EstadoRoutingModule {}
