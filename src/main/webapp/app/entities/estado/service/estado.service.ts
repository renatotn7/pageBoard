import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEstado, getEstadoIdentifier } from '../estado.model';

export type EntityResponseType = HttpResponse<IEstado>;
export type EntityArrayResponseType = HttpResponse<IEstado[]>;

@Injectable({ providedIn: 'root' })
export class EstadoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/estados');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(estado: IEstado): Observable<EntityResponseType> {
    return this.http.post<IEstado>(this.resourceUrl, estado, { observe: 'response' });
  }

  update(estado: IEstado): Observable<EntityResponseType> {
    return this.http.put<IEstado>(`${this.resourceUrl}/${getEstadoIdentifier(estado) as number}`, estado, { observe: 'response' });
  }

  partialUpdate(estado: IEstado): Observable<EntityResponseType> {
    return this.http.patch<IEstado>(`${this.resourceUrl}/${getEstadoIdentifier(estado) as number}`, estado, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEstado>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEstado[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEstadoToCollectionIfMissing(estadoCollection: IEstado[], ...estadosToCheck: (IEstado | null | undefined)[]): IEstado[] {
    const estados: IEstado[] = estadosToCheck.filter(isPresent);
    if (estados.length > 0) {
      const estadoCollectionIdentifiers = estadoCollection.map(estadoItem => getEstadoIdentifier(estadoItem)!);
      const estadosToAdd = estados.filter(estadoItem => {
        const estadoIdentifier = getEstadoIdentifier(estadoItem);
        if (estadoIdentifier == null || estadoCollectionIdentifiers.includes(estadoIdentifier)) {
          return false;
        }
        estadoCollectionIdentifiers.push(estadoIdentifier);
        return true;
      });
      return [...estadosToAdd, ...estadoCollection];
    }
    return estadoCollection;
  }
}
