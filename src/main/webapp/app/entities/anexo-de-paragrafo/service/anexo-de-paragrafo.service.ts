import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAnexoDeParagrafo, getAnexoDeParagrafoIdentifier } from '../anexo-de-paragrafo.model';

export type EntityResponseType = HttpResponse<IAnexoDeParagrafo>;
export type EntityArrayResponseType = HttpResponse<IAnexoDeParagrafo[]>;

@Injectable({ providedIn: 'root' })
export class AnexoDeParagrafoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/anexo-de-paragrafos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(anexoDeParagrafo: IAnexoDeParagrafo): Observable<EntityResponseType> {
    return this.http.post<IAnexoDeParagrafo>(this.resourceUrl, anexoDeParagrafo, { observe: 'response' });
  }

  update(anexoDeParagrafo: IAnexoDeParagrafo): Observable<EntityResponseType> {
    return this.http.put<IAnexoDeParagrafo>(
      `${this.resourceUrl}/${getAnexoDeParagrafoIdentifier(anexoDeParagrafo) as number}`,
      anexoDeParagrafo,
      { observe: 'response' }
    );
  }

  partialUpdate(anexoDeParagrafo: IAnexoDeParagrafo): Observable<EntityResponseType> {
    return this.http.patch<IAnexoDeParagrafo>(
      `${this.resourceUrl}/${getAnexoDeParagrafoIdentifier(anexoDeParagrafo) as number}`,
      anexoDeParagrafo,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAnexoDeParagrafo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAnexoDeParagrafo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAnexoDeParagrafoToCollectionIfMissing(
    anexoDeParagrafoCollection: IAnexoDeParagrafo[],
    ...anexoDeParagrafosToCheck: (IAnexoDeParagrafo | null | undefined)[]
  ): IAnexoDeParagrafo[] {
    const anexoDeParagrafos: IAnexoDeParagrafo[] = anexoDeParagrafosToCheck.filter(isPresent);
    if (anexoDeParagrafos.length > 0) {
      const anexoDeParagrafoCollectionIdentifiers = anexoDeParagrafoCollection.map(
        anexoDeParagrafoItem => getAnexoDeParagrafoIdentifier(anexoDeParagrafoItem)!
      );
      const anexoDeParagrafosToAdd = anexoDeParagrafos.filter(anexoDeParagrafoItem => {
        const anexoDeParagrafoIdentifier = getAnexoDeParagrafoIdentifier(anexoDeParagrafoItem);
        if (anexoDeParagrafoIdentifier == null || anexoDeParagrafoCollectionIdentifiers.includes(anexoDeParagrafoIdentifier)) {
          return false;
        }
        anexoDeParagrafoCollectionIdentifiers.push(anexoDeParagrafoIdentifier);
        return true;
      });
      return [...anexoDeParagrafosToAdd, ...anexoDeParagrafoCollection];
    }
    return anexoDeParagrafoCollection;
  }
}
