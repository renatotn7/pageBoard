import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPergunta, Pergunta } from '../pergunta.model';
import { PerguntaService } from '../service/pergunta.service';

@Injectable({ providedIn: 'root' })
export class PerguntaRoutingResolveService implements Resolve<IPergunta> {
  constructor(protected service: PerguntaService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPergunta> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((pergunta: HttpResponse<Pergunta>) => {
          if (pergunta.body) {
            return of(pergunta.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Pergunta());
  }
}
