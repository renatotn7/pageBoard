import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IParagrafo, Paragrafo } from '../paragrafo.model';
import { ParagrafoService } from '../service/paragrafo.service';

@Injectable({ providedIn: 'root' })
export class ParagrafoRoutingResolveService implements Resolve<IParagrafo> {
  constructor(protected service: ParagrafoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IParagrafo> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((paragrafo: HttpResponse<Paragrafo>) => {
          if (paragrafo.body) {
            return of(paragrafo.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Paragrafo());
  }
}
