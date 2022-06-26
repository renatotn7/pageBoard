import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPais, getPaisIdentifier } from '../pais.model';

export type EntityResponseType = HttpResponse<IPais>;
export type EntityArrayResponseType = HttpResponse<IPais[]>;

@Injectable({ providedIn: 'root' })
export class PaisService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/pais');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(pais: IPais): Observable<EntityResponseType> {
    return this.http.post<IPais>(this.resourceUrl, pais, { observe: 'response' });
  }

  update(pais: IPais): Observable<EntityResponseType> {
    return this.http.put<IPais>(`${this.resourceUrl}/${getPaisIdentifier(pais) as number}`, pais, { observe: 'response' });
  }

  partialUpdate(pais: IPais): Observable<EntityResponseType> {
    return this.http.patch<IPais>(`${this.resourceUrl}/${getPaisIdentifier(pais) as number}`, pais, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPais>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPais[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPaisToCollectionIfMissing(paisCollection: IPais[], ...paisToCheck: (IPais | null | undefined)[]): IPais[] {
    const pais: IPais[] = paisToCheck.filter(isPresent);
    if (pais.length > 0) {
      const paisCollectionIdentifiers = paisCollection.map(paisItem => getPaisIdentifier(paisItem)!);
      const paisToAdd = pais.filter(paisItem => {
        const paisIdentifier = getPaisIdentifier(paisItem);
        if (paisIdentifier == null || paisCollectionIdentifiers.includes(paisIdentifier)) {
          return false;
        }
        paisCollectionIdentifiers.push(paisIdentifier);
        return true;
      });
      return [...paisToAdd, ...paisCollection];
    }
    return paisCollection;
  }
}
