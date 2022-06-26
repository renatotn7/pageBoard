import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPageBoard, PageBoard } from '../page-board.model';
import { PageBoardService } from '../service/page-board.service';

@Injectable({ providedIn: 'root' })
export class PageBoardRoutingResolveService implements Resolve<IPageBoard> {
  constructor(protected service: PageBoardService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPageBoard> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((pageBoard: HttpResponse<PageBoard>) => {
          if (pageBoard.body) {
            return of(pageBoard.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new PageBoard());
  }
}
