import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IResposta, getRespostaIdentifier } from '../resposta.model';

export type EntityResponseType = HttpResponse<IResposta>;
export type EntityArrayResponseType = HttpResponse<IResposta[]>;

@Injectable({ providedIn: 'root' })
export class RespostaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/respostas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(resposta: IResposta): Observable<EntityResponseType> {
    return this.http.post<IResposta>(this.resourceUrl, resposta, { observe: 'response' });
  }

  update(resposta: IResposta): Observable<EntityResponseType> {
    return this.http.put<IResposta>(`${this.resourceUrl}/${getRespostaIdentifier(resposta) as number}`, resposta, { observe: 'response' });
  }

  partialUpdate(resposta: IResposta): Observable<EntityResponseType> {
    return this.http.patch<IResposta>(`${this.resourceUrl}/${getRespostaIdentifier(resposta) as number}`, resposta, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IResposta>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IResposta[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRespostaToCollectionIfMissing(respostaCollection: IResposta[], ...respostasToCheck: (IResposta | null | undefined)[]): IResposta[] {
    const respostas: IResposta[] = respostasToCheck.filter(isPresent);
    if (respostas.length > 0) {
      const respostaCollectionIdentifiers = respostaCollection.map(respostaItem => getRespostaIdentifier(respostaItem)!);
      const respostasToAdd = respostas.filter(respostaItem => {
        const respostaIdentifier = getRespostaIdentifier(respostaItem);
        if (respostaIdentifier == null || respostaCollectionIdentifiers.includes(respostaIdentifier)) {
          return false;
        }
        respostaCollectionIdentifiers.push(respostaIdentifier);
        return true;
      });
      return [...respostasToAdd, ...respostaCollection];
    }
    return respostaCollection;
  }
}
