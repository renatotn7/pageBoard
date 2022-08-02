import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PaginaComponent } from '../list/pagina.component';
import { PaginaDetailComponent } from '../detail/pagina-detail.component';
import { PaginaUpdateComponent } from '../update/pagina-update.component';
import { PaginaRoutingResolveService } from './pagina-routing-resolve.service';
import { PageBoardComponent } from '../../page-board/list/page-board.component';

const paginaRoute: Routes = [
  {
    path: '',
    component: PaginaComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PaginaDetailComponent,
    resolve: {
      pagina: PaginaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PaginaUpdateComponent,
    resolve: {
      pagina: PaginaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PaginaUpdateComponent,
    resolve: {
      pagina: PaginaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    data: { pageTitle: 'pageBoardApp.pageBoard.home.title' },
    loadChildren: () => import('../../page-board/page-board.module').then(m => m.PageBoardModule),
  },
  {
    path: ':id/editpb',
    component: PageBoardComponent,
    resolve: {
      pagina: PaginaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(paginaRoute)],
  exports: [RouterModule],
})
export class PaginaRoutingModule {}
