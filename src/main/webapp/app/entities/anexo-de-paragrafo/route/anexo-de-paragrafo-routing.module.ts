import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AnexoDeParagrafoComponent } from '../list/anexo-de-paragrafo.component';
import { AnexoDeParagrafoDetailComponent } from '../detail/anexo-de-paragrafo-detail.component';
import { AnexoDeParagrafoUpdateComponent } from '../update/anexo-de-paragrafo-update.component';
import { AnexoDeParagrafoRoutingResolveService } from './anexo-de-paragrafo-routing-resolve.service';

const anexoDeParagrafoRoute: Routes = [
  {
    path: '',
    component: AnexoDeParagrafoComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AnexoDeParagrafoDetailComponent,
    resolve: {
      anexoDeParagrafo: AnexoDeParagrafoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AnexoDeParagrafoUpdateComponent,
    resolve: {
      anexoDeParagrafo: AnexoDeParagrafoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AnexoDeParagrafoUpdateComponent,
    resolve: {
      anexoDeParagrafo: AnexoDeParagrafoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(anexoDeParagrafoRoute)],
  exports: [RouterModule],
})
export class AnexoDeParagrafoRoutingModule {}
