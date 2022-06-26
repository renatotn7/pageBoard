import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ParagrafoComponent } from '../list/paragrafo.component';
import { ParagrafoDetailComponent } from '../detail/paragrafo-detail.component';
import { ParagrafoUpdateComponent } from '../update/paragrafo-update.component';
import { ParagrafoRoutingResolveService } from './paragrafo-routing-resolve.service';

const paragrafoRoute: Routes = [
  {
    path: '',
    component: ParagrafoComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ParagrafoDetailComponent,
    resolve: {
      paragrafo: ParagrafoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ParagrafoUpdateComponent,
    resolve: {
      paragrafo: ParagrafoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ParagrafoUpdateComponent,
    resolve: {
      paragrafo: ParagrafoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(paragrafoRoute)],
  exports: [RouterModule],
})
export class ParagrafoRoutingModule {}
