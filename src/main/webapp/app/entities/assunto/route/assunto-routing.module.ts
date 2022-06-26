import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AssuntoComponent } from '../list/assunto.component';
import { AssuntoDetailComponent } from '../detail/assunto-detail.component';
import { AssuntoUpdateComponent } from '../update/assunto-update.component';
import { AssuntoRoutingResolveService } from './assunto-routing-resolve.service';

const assuntoRoute: Routes = [
  {
    path: '',
    component: AssuntoComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AssuntoDetailComponent,
    resolve: {
      assunto: AssuntoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AssuntoUpdateComponent,
    resolve: {
      assunto: AssuntoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AssuntoUpdateComponent,
    resolve: {
      assunto: AssuntoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(assuntoRoute)],
  exports: [RouterModule],
})
export class AssuntoRoutingModule {}
