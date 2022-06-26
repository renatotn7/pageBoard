import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LivroComponent } from '../list/livro.component';
import { LivroDetailComponent } from '../detail/livro-detail.component';
import { LivroUpdateComponent } from '../update/livro-update.component';
import { LivroRoutingResolveService } from './livro-routing-resolve.service';

const livroRoute: Routes = [
  {
    path: '',
    component: LivroComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LivroDetailComponent,
    resolve: {
      livro: LivroRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LivroUpdateComponent,
    resolve: {
      livro: LivroRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LivroUpdateComponent,
    resolve: {
      livro: LivroRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(livroRoute)],
  exports: [RouterModule],
})
export class LivroRoutingModule {}
