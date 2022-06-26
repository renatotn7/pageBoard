import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProjeto, getProjetoIdentifier } from '../projeto.model';

export type EntityResponseType = HttpResponse<IProjeto>;
export type EntityArrayResponseType = HttpResponse<IProjeto[]>;

@Injectable({ providedIn: 'root' })
export class ProjetoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/projetos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(projeto: IProjeto): Observable<EntityResponseType> {
    return this.http.post<IProjeto>(this.resourceUrl, projeto, { observe: 'response' });
  }

  update(projeto: IProjeto): Observable<EntityResponseType> {
    return this.http.put<IProjeto>(`${this.resourceUrl}/${getProjetoIdentifier(projeto) as number}`, projeto, { observe: 'response' });
  }

  partialUpdate(projeto: IProjeto): Observable<EntityResponseType> {
    return this.http.patch<IProjeto>(`${this.resourceUrl}/${getProjetoIdentifier(projeto) as number}`, projeto, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProjeto>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProjeto[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addProjetoToCollectionIfMissing(projetoCollection: IProjeto[], ...projetosToCheck: (IProjeto | null | undefined)[]): IProjeto[] {
    const projetos: IProjeto[] = projetosToCheck.filter(isPresent);
    if (projetos.length > 0) {
      const projetoCollectionIdentifiers = projetoCollection.map(projetoItem => getProjetoIdentifier(projetoItem)!);
      const projetosToAdd = projetos.filter(projetoItem => {
        const projetoIdentifier = getProjetoIdentifier(projetoItem);
        if (projetoIdentifier == null || projetoCollectionIdentifiers.includes(projetoIdentifier)) {
          return false;
        }
        projetoCollectionIdentifiers.push(projetoIdentifier);
        return true;
      });
      return [...projetosToAdd, ...projetoCollection];
    }
    return projetoCollection;
  }
}
