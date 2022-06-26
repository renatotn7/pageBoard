import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPagina, Pagina } from '../pagina.model';
import { PaginaService } from '../service/pagina.service';

@Injectable({ providedIn: 'root' })
export class PaginaRoutingResolveService implements Resolve<IPagina> {
  constructor(protected service: PaginaService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPagina> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((pagina: HttpResponse<Pagina>) => {
          if (pagina.body) {
            return of(pagina.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Pagina());
  }
}
