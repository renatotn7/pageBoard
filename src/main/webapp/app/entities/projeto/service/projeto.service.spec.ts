import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProjeto, Projeto } from '../projeto.model';

import { ProjetoService } from './projeto.service';

describe('Projeto Service', () => {
  let service: ProjetoService;
  let httpMock: HttpTestingController;
  let elemDefault: IProjeto;
  let expectedResult: IProjeto | IProjeto[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProjetoService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nome: 'AAAAAAA',
      descricao: 'AAAAAAA',
      imagemContentType: 'image/png',
      imagem: 'AAAAAAA',
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

    it('should create a Projeto', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Projeto()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Projeto', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nome: 'BBBBBB',
          descricao: 'BBBBBB',
          imagem: 'BBBBBB',
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

    it('should partial update a Projeto', () => {
      const patchObject = Object.assign(
        {
          nome: 'BBBBBB',
          imagem: 'BBBBBB',
        },
        new Projeto()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Projeto', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nome: 'BBBBBB',
          descricao: 'BBBBBB',
          imagem: 'BBBBBB',
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

    it('should delete a Projeto', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addProjetoToCollectionIfMissing', () => {
      it('should add a Projeto to an empty array', () => {
        const projeto: IProjeto = { id: 123 };
        expectedResult = service.addProjetoToCollectionIfMissing([], projeto);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projeto);
      });

      it('should not add a Projeto to an array that contains it', () => {
        const projeto: IProjeto = { id: 123 };
        const projetoCollection: IProjeto[] = [
          {
            ...projeto,
          },
          { id: 456 },
        ];
        expectedResult = service.addProjetoToCollectionIfMissing(projetoCollection, projeto);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Projeto to an array that doesn't contain it", () => {
        const projeto: IProjeto = { id: 123 };
        const projetoCollection: IProjeto[] = [{ id: 456 }];
        expectedResult = service.addProjetoToCollectionIfMissing(projetoCollection, projeto);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projeto);
      });

      it('should add only unique Projeto to an array', () => {
        const projetoArray: IProjeto[] = [{ id: 123 }, { id: 456 }, { id: 21365 }];
        const projetoCollection: IProjeto[] = [{ id: 123 }];
        expectedResult = service.addProjetoToCollectionIfMissing(projetoCollection, ...projetoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const projeto: IProjeto = { id: 123 };
        const projeto2: IProjeto = { id: 456 };
        expectedResult = service.addProjetoToCollectionIfMissing([], projeto, projeto2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(projeto);
        expect(expectedResult).toContain(projeto2);
      });

      it('should accept null and undefined values', () => {
        const projeto: IProjeto = { id: 123 };
        expectedResult = service.addProjetoToCollectionIfMissing([], null, projeto, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(projeto);
      });

      it('should return initial array if no Projeto is added', () => {
        const projetoCollection: IProjeto[] = [{ id: 123 }];
        expectedResult = service.addProjetoToCollectionIfMissing(projetoCollection, undefined, null);
        expect(expectedResult).toEqual(projetoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
