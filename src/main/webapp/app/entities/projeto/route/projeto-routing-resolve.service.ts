import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProjeto, Projeto } from '../projeto.model';
import { ProjetoService } from '../service/projeto.service';

@Injectable({ providedIn: 'root' })
export class ProjetoRoutingResolveService implements Resolve<IProjeto> {
  constructor(protected service: ProjetoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProjeto> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((projeto: HttpResponse<Projeto>) => {
          if (projeto.body) {
            return of(projeto.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Projeto());
  }
}
