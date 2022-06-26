import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProjetoComponent } from '../list/projeto.component';
import { ProjetoDetailComponent } from '../detail/projeto-detail.component';
import { ProjetoUpdateComponent } from '../update/projeto-update.component';
import { ProjetoRoutingResolveService } from './projeto-routing-resolve.service';

const projetoRoute: Routes = [
  {
    path: '',
    component: ProjetoComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProjetoDetailComponent,
    resolve: {
      projeto: ProjetoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProjetoUpdateComponent,
    resolve: {
      projeto: ProjetoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProjetoUpdateComponent,
    resolve: {
      projeto: ProjetoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(projetoRoute)],
  exports: [RouterModule],
})
export class ProjetoRoutingModule {}
