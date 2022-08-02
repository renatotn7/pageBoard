import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPagina, getPaginaIdentifier } from '../pagina.model';
import { IParagrafo } from '../../paragrafo/paragrafo.model';
import { IAnexoDeParagrafo } from '../../anexo-de-paragrafo/anexo-de-paragrafo.model';

export type EntityResponseType = HttpResponse<IPagina>;
export type EntityArrayResponseType = HttpResponse<IPagina[]>;

@Injectable({ providedIn: 'root' })
export class PaginaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/paginas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(pagina: IPagina): Observable<EntityResponseType> {
    return this.http.post<IPagina>(this.resourceUrl, pagina, { observe: 'response' });
  }

  update(pagina: IPagina): Observable<EntityResponseType> {
    return this.http.put<IPagina>(`${this.resourceUrl}/${getPaginaIdentifier(pagina) as number}`, pagina, { observe: 'response' });
  }

  createBlocos(pagina: IPagina): Observable<HttpResponse<IParagrafo[]>> {
    return this.http.post<IParagrafo[]>(`${this.resourceUrl}/bloco/criaBlocosDeTexto`, pagina, { observe: 'response' });
  }
  generatePerguntas(paragrafo: IParagrafo): Observable<HttpResponse<IParagrafo>> {
    return this.http.post<IParagrafo>(`${this.resourceUrl}/bloco/perguntasDiscursivas`, paragrafo, { observe: 'response' });
  }
  generateTextoFacilitado(paragrafo: IParagrafo): Observable<HttpResponse<IParagrafo>> {
    return this.http.post<IParagrafo>(`${this.resourceUrl}/bloco/textoSimplificado`, paragrafo, { observe: 'response' });
  }
  generateEmTopicos(paragrafo: IParagrafo): Observable<HttpResponse<IParagrafo>> {
    return this.http.post<IParagrafo>(`${this.resourceUrl}/bloco/topicos`, paragrafo, { observe: 'response' });
  }
  generateEmTitulos(paragrafo: IParagrafo): Observable<HttpResponse<IParagrafo>> {
    return this.http.post<IParagrafo>(`${this.resourceUrl}/bloco/explicaEmTitulos`, paragrafo, { observe: 'response' });
  }
  partialUpdate(pagina: IPagina): Observable<EntityResponseType> {
    return this.http.patch<IPagina>(`${this.resourceUrl}/${getPaginaIdentifier(pagina) as number}`, pagina, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPagina>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPagina[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPaginaToCollectionIfMissing(paginaCollection: IPagina[], ...paginasToCheck: (IPagina | null | undefined)[]): IPagina[] {
    const paginas: IPagina[] = paginasToCheck.filter(isPresent);
    if (paginas.length > 0) {
      const paginaCollectionIdentifiers = paginaCollection.map(paginaItem => getPaginaIdentifier(paginaItem)!);
      const paginasToAdd = paginas.filter(paginaItem => {
        const paginaIdentifier = getPaginaIdentifier(paginaItem);
        if (paginaIdentifier == null || paginaCollectionIdentifiers.includes(paginaIdentifier)) {
          return false;
        }
        paginaCollectionIdentifiers.push(paginaIdentifier);
        return true;
      });
      return [...paginasToAdd, ...paginaCollection];
    }
    return paginaCollection;
  }
}
