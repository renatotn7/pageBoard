import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAssunto, getAssuntoIdentifier } from '../assunto.model';

export type EntityResponseType = HttpResponse<IAssunto>;
export type EntityArrayResponseType = HttpResponse<IAssunto[]>;

@Injectable({ providedIn: 'root' })
export class AssuntoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/assuntos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(assunto: IAssunto): Observable<EntityResponseType> {
    return this.http.post<IAssunto>(this.resourceUrl, assunto, { observe: 'response' });
  }

  update(assunto: IAssunto): Observable<EntityResponseType> {
    return this.http.put<IAssunto>(`${this.resourceUrl}/${getAssuntoIdentifier(assunto) as number}`, assunto, { observe: 'response' });
  }

  partialUpdate(assunto: IAssunto): Observable<EntityResponseType> {
    return this.http.patch<IAssunto>(`${this.resourceUrl}/${getAssuntoIdentifier(assunto) as number}`, assunto, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAssunto>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAssunto[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAssuntoToCollectionIfMissing(assuntoCollection: IAssunto[], ...assuntosToCheck: (IAssunto | null | undefined)[]): IAssunto[] {
    const assuntos: IAssunto[] = assuntosToCheck.filter(isPresent);
    if (assuntos.length > 0) {
      const assuntoCollectionIdentifiers = assuntoCollection.map(assuntoItem => getAssuntoIdentifier(assuntoItem)!);
      const assuntosToAdd = assuntos.filter(assuntoItem => {
        const assuntoIdentifier = getAssuntoIdentifier(assuntoItem);
        if (assuntoIdentifier == null || assuntoCollectionIdentifiers.includes(assuntoIdentifier)) {
          return false;
        }
        assuntoCollectionIdentifiers.push(assuntoIdentifier);
        return true;
      });
      return [...assuntosToAdd, ...assuntoCollection];
    }
    return assuntoCollection;
  }
}
