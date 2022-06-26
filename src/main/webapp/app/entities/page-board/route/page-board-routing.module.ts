import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PageBoardComponent } from '../list/page-board.component';
import { PageBoardDetailComponent } from '../detail/page-board-detail.component';
import { PageBoardUpdateComponent } from '../update/page-board-update.component';
import { PageBoardRoutingResolveService } from './page-board-routing-resolve.service';

const pageBoardRoute: Routes = [
  {
    path: '',
    component: PageBoardComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PageBoardDetailComponent,
    resolve: {
      pageBoard: PageBoardRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PageBoardUpdateComponent,
    resolve: {
      pageBoard: PageBoardRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PageBoardUpdateComponent,
    resolve: {
      pageBoard: PageBoardRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(pageBoardRoute)],
  exports: [RouterModule],
})
export class PageBoardRoutingModule {}
