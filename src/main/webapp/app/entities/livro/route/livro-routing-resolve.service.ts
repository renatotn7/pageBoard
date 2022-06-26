import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILivro, Livro } from '../livro.model';
import { LivroService } from '../service/livro.service';

@Injectable({ providedIn: 'root' })
export class LivroRoutingResolveService implements Resolve<ILivro> {
  constructor(protected service: LivroService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILivro> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((livro: HttpResponse<Livro>) => {
          if (livro.body) {
            return of(livro.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Livro());
  }
}
