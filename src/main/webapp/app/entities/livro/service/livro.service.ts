import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILivro, getLivroIdentifier } from '../livro.model';

export type EntityResponseType = HttpResponse<ILivro>;
export type EntityArrayResponseType = HttpResponse<ILivro[]>;

@Injectable({ providedIn: 'root' })
export class LivroService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/livros');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(livro: ILivro): Observable<EntityResponseType> {
    return this.http.post<ILivro>(this.resourceUrl, livro, { observe: 'response' });
  }

  update(livro: ILivro): Observable<EntityResponseType> {
    return this.http.put<ILivro>(`${this.resourceUrl}/${getLivroIdentifier(livro) as number}`, livro, { observe: 'response' });
  }

  partialUpdate(livro: ILivro): Observable<EntityResponseType> {
    return this.http.patch<ILivro>(`${this.resourceUrl}/${getLivroIdentifier(livro) as number}`, livro, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILivro>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILivro[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLivroToCollectionIfMissing(livroCollection: ILivro[], ...livrosToCheck: (ILivro | null | undefined)[]): ILivro[] {
    const livros: ILivro[] = livrosToCheck.filter(isPresent);
    if (livros.length > 0) {
      const livroCollectionIdentifiers = livroCollection.map(livroItem => getLivroIdentifier(livroItem)!);
      const livrosToAdd = livros.filter(livroItem => {
        const livroIdentifier = getLivroIdentifier(livroItem);
        if (livroIdentifier == null || livroCollectionIdentifiers.includes(livroIdentifier)) {
          return false;
        }
        livroCollectionIdentifiers.push(livroIdentifier);
        return true;
      });
      return [...livrosToAdd, ...livroCollection];
    }
    return livroCollection;
  }
}
