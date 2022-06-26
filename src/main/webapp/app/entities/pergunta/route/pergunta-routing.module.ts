import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PerguntaComponent } from '../list/pergunta.component';
import { PerguntaDetailComponent } from '../detail/pergunta-detail.component';
import { PerguntaUpdateComponent } from '../update/pergunta-update.component';
import { PerguntaRoutingResolveService } from './pergunta-routing-resolve.service';

const perguntaRoute: Routes = [
  {
    path: '',
    component: PerguntaComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PerguntaDetailComponent,
    resolve: {
      pergunta: PerguntaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PerguntaUpdateComponent,
    resolve: {
      pergunta: PerguntaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PerguntaUpdateComponent,
    resolve: {
      pergunta: PerguntaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(perguntaRoute)],
  exports: [RouterModule],
})
export class PerguntaRoutingModule {}
