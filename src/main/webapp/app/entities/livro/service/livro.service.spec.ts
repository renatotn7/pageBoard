import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ILivro, Livro } from '../livro.model';

import { LivroService } from './livro.service';

describe('Livro Service', () => {
  let service: LivroService;
  let httpMock: HttpTestingController;
  let elemDefault: ILivro;
  let expectedResult: ILivro | ILivro[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LivroService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nomeLivro: 'AAAAAAA',
      editora: 'AAAAAAA',
      autor: 'AAAAAAA',
      anoDePublicacao: 0,
      tags: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Livro', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Livro()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Livro', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomeLivro: 'BBBBBB',
          editora: 'BBBBBB',
          autor: 'BBBBBB',
          anoDePublicacao: 1,
          tags: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Livro', () => {
      const patchObject = Object.assign(
        {
          nomeLivro: 'BBBBBB',
          tags: 'BBBBBB',
        },
        new Livro()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Livro', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomeLivro: 'BBBBBB',
          editora: 'BBBBBB',
          autor: 'BBBBBB',
          anoDePublicacao: 1,
          tags: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Livro', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addLivroToCollectionIfMissing', () => {
      it('should add a Livro to an empty array', () => {
        const livro: ILivro = { id: 123 };
        expectedResult = service.addLivroToCollectionIfMissing([], livro);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(livro);
      });

      it('should not add a Livro to an array that contains it', () => {
        const livro: ILivro = { id: 123 };
        const livroCollection: ILivro[] = [
          {
            ...livro,
          },
          { id: 456 },
        ];
        expectedResult = service.addLivroToCollectionIfMissing(livroCollection, livro);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Livro to an array that doesn't contain it", () => {
        const livro: ILivro = { id: 123 };
        const livroCollection: ILivro[] = [{ id: 456 }];
        expectedResult = service.addLivroToCollectionIfMissing(livroCollection, livro);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(livro);
      });

      it('should add only unique Livro to an array', () => {
        const livroArray: ILivro[] = [{ id: 123 }, { id: 456 }, { id: 3567 }];
        const livroCollection: ILivro[] = [{ id: 123 }];
        expectedResult = service.addLivroToCollectionIfMissing(livroCollection, ...livroArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const livro: ILivro = { id: 123 };
        const livro2: ILivro = { id: 456 };
        expectedResult = service.addLivroToCollectionIfMissing([], livro, livro2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(livro);
        expect(expectedResult).toContain(livro2);
      });

      it('should accept null and undefined values', () => {
        const livro: ILivro = { id: 123 };
        expectedResult = service.addLivroToCollectionIfMissing([], null, livro, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(livro);
      });

      it('should return initial array if no Livro is added', () => {
        const livroCollection: ILivro[] = [{ id: 123 }];
        expectedResult = service.addLivroToCollectionIfMissing(livroCollection, undefined, null);
        expect(expectedResult).toEqual(livroCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
