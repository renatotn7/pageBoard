import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IParagrafo, getParagrafoIdentifier } from '../paragrafo.model';

export type EntityResponseType = HttpResponse<IParagrafo>;
export type EntityArrayResponseType = HttpResponse<IParagrafo[]>;

@Injectable({ providedIn: 'root' })
export class ParagrafoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/paragrafos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(paragrafo: IParagrafo): Observable<EntityResponseType> {
    return this.http.post<IParagrafo>(this.resourceUrl, paragrafo, { observe: 'response' });
  }

  update(paragrafo: IParagrafo): Observable<EntityResponseType> {
    return this.http.put<IParagrafo>(`${this.resourceUrl}/${getParagrafoIdentifier(paragrafo) as number}`, paragrafo, {
      observe: 'response',
    });
  }

  partialUpdate(paragrafo: IParagrafo): Observable<EntityResponseType> {
    return this.http.patch<IParagrafo>(`${this.resourceUrl}/${getParagrafoIdentifier(paragrafo) as number}`, paragrafo, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IParagrafo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IParagrafo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addParagrafoToCollectionIfMissing(
    paragrafoCollection: IParagrafo[],
    ...paragrafosToCheck: (IParagrafo | null | undefined)[]
  ): IParagrafo[] {
    const paragrafos: IParagrafo[] = paragrafosToCheck.filter(isPresent);
    if (paragrafos.length > 0) {
      const paragrafoCollectionIdentifiers = paragrafoCollection.map(paragrafoItem => getParagrafoIdentifier(paragrafoItem)!);
      const paragrafosToAdd = paragrafos.filter(paragrafoItem => {
        const paragrafoIdentifier = getParagrafoIdentifier(paragrafoItem);
        if (paragrafoIdentifier == null || paragrafoCollectionIdentifiers.includes(paragrafoIdentifier)) {
          return false;
        }
        paragrafoCollectionIdentifiers.push(paragrafoIdentifier);
        return true;
      });
      return [...paragrafosToAdd, ...paragrafoCollection];
    }
    return paragrafoCollection;
  }
}
