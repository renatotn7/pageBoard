import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPageBoard, getPageBoardIdentifier } from '../page-board.model';

export type EntityResponseType = HttpResponse<IPageBoard>;
export type EntityArrayResponseType = HttpResponse<IPageBoard[]>;

@Injectable({ providedIn: 'root' })
export class PageBoardService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/page-boards');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(pageBoard: IPageBoard): Observable<EntityResponseType> {
    return this.http.post<IPageBoard>(this.resourceUrl, pageBoard, { observe: 'response' });
  }

  update(pageBoard: IPageBoard): Observable<EntityResponseType> {
    return this.http.put<IPageBoard>(`${this.resourceUrl}/${getPageBoardIdentifier(pageBoard) as number}`, pageBoard, {
      observe: 'response',
    });
  }

  partialUpdate(pageBoard: IPageBoard): Observable<EntityResponseType> {
    return this.http.patch<IPageBoard>(`${this.resourceUrl}/${getPageBoardIdentifier(pageBoard) as number}`, pageBoard, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPageBoard>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPageBoard[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPageBoardToCollectionIfMissing(
    pageBoardCollection: IPageBoard[],
    ...pageBoardsToCheck: (IPageBoard | null | undefined)[]
  ): IPageBoard[] {
    const pageBoards: IPageBoard[] = pageBoardsToCheck.filter(isPresent);
    if (pageBoards.length > 0) {
      const pageBoardCollectionIdentifiers = pageBoardCollection.map(pageBoardItem => getPageBoardIdentifier(pageBoardItem)!);
      const pageBoardsToAdd = pageBoards.filter(pageBoardItem => {
        const pageBoardIdentifier = getPageBoardIdentifier(pageBoardItem);
        if (pageBoardIdentifier == null || pageBoardCollectionIdentifiers.includes(pageBoardIdentifier)) {
          return false;
        }
        pageBoardCollectionIdentifiers.push(pageBoardIdentifier);
        return true;
      });
      return [...pageBoardsToAdd, ...pageBoardCollection];
    }
    return pageBoardCollection;
  }
}
