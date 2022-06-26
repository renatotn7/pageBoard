import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPais, Pais } from '../pais.model';
import { PaisService } from '../service/pais.service';

@Injectable({ providedIn: 'root' })
export class PaisRoutingResolveService implements Resolve<IPais> {
  constructor(protected service: PaisService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPais> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((pais: HttpResponse<Pais>) => {
          if (pais.body) {
            return of(pais.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Pais());
  }
}
