import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IResposta, Resposta } from '../resposta.model';
import { RespostaService } from '../service/resposta.service';

@Injectable({ providedIn: 'root' })
export class RespostaRoutingResolveService implements Resolve<IResposta> {
  constructor(protected service: RespostaService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IResposta> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((resposta: HttpResponse<Resposta>) => {
          if (resposta.body) {
            return of(resposta.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Resposta());
  }
}
