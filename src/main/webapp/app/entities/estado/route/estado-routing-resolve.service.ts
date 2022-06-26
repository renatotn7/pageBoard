import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEstado, Estado } from '../estado.model';
import { EstadoService } from '../service/estado.service';

@Injectable({ providedIn: 'root' })
export class EstadoRoutingResolveService implements Resolve<IEstado> {
  constructor(protected service: EstadoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEstado> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((estado: HttpResponse<Estado>) => {
          if (estado.body) {
            return of(estado.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Estado());
  }
}
