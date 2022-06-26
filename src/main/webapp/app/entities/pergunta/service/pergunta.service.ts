import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPergunta, getPerguntaIdentifier } from '../pergunta.model';

export type EntityResponseType = HttpResponse<IPergunta>;
export type EntityArrayResponseType = HttpResponse<IPergunta[]>;

@Injectable({ providedIn: 'root' })
export class PerguntaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/perguntas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(pergunta: IPergunta): Observable<EntityResponseType> {
    return this.http.post<IPergunta>(this.resourceUrl, pergunta, { observe: 'response' });
  }

  update(pergunta: IPergunta): Observable<EntityResponseType> {
    return this.http.put<IPergunta>(`${this.resourceUrl}/${getPerguntaIdentifier(pergunta) as number}`, pergunta, { observe: 'response' });
  }

  partialUpdate(pergunta: IPergunta): Observable<EntityResponseType> {
    return this.http.patch<IPergunta>(`${this.resourceUrl}/${getPerguntaIdentifier(pergunta) as number}`, pergunta, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPergunta>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPergunta[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPerguntaToCollectionIfMissing(perguntaCollection: IPergunta[], ...perguntasToCheck: (IPergunta | null | undefined)[]): IPergunta[] {
    const perguntas: IPergunta[] = perguntasToCheck.filter(isPresent);
    if (perguntas.length > 0) {
      const perguntaCollectionIdentifiers = perguntaCollection.map(perguntaItem => getPerguntaIdentifier(perguntaItem)!);
      const perguntasToAdd = perguntas.filter(perguntaItem => {
        const perguntaIdentifier = getPerguntaIdentifier(perguntaItem);
        if (perguntaIdentifier == null || perguntaCollectionIdentifiers.includes(perguntaIdentifier)) {
          return false;
        }
        perguntaCollectionIdentifiers.push(perguntaIdentifier);
        return true;
      });
      return [...perguntasToAdd, ...perguntaCollection];
    }
    return perguntaCollection;
  }
}
