import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAnexoDeParagrafo, AnexoDeParagrafo } from '../anexo-de-paragrafo.model';
import { AnexoDeParagrafoService } from '../service/anexo-de-paragrafo.service';

@Injectable({ providedIn: 'root' })
export class AnexoDeParagrafoRoutingResolveService implements Resolve<IAnexoDeParagrafo> {
  constructor(protected service: AnexoDeParagrafoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAnexoDeParagrafo> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((anexoDeParagrafo: HttpResponse<AnexoDeParagrafo>) => {
          if (anexoDeParagrafo.body) {
            return of(anexoDeParagrafo.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new AnexoDeParagrafo());
  }
}
