import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RespostaComponent } from '../list/resposta.component';
import { RespostaDetailComponent } from '../detail/resposta-detail.component';
import { RespostaUpdateComponent } from '../update/resposta-update.component';
import { RespostaRoutingResolveService } from './resposta-routing-resolve.service';

const respostaRoute: Routes = [
  {
    path: '',
    component: RespostaComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RespostaDetailComponent,
    resolve: {
      resposta: RespostaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RespostaUpdateComponent,
    resolve: {
      resposta: RespostaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RespostaUpdateComponent,
    resolve: {
      resposta: RespostaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(respostaRoute)],
  exports: [RouterModule],
})
export class RespostaRoutingModule {}
