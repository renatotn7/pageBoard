import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPagina, Pagina } from '../pagina.model';

import { PaginaService } from './pagina.service';

describe('Pagina Service', () => {
  let service: PaginaService;
  let httpMock: HttpTestingController;
  let elemDefault: IPagina;
  let expectedResult: IPagina | IPagina[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PaginaService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      numero: 0,
      texto: 'AAAAAAA',
      planoDeAula: 'AAAAAAA',
      imagemContentType: 'image/png',
      imagem: 'AAAAAAA',
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

    it('should create a Pagina', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Pagina()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Pagina', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          numero: 1,
          texto: 'BBBBBB',
          planoDeAula: 'BBBBBB',
          imagem: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Pagina', () => {
      const patchObject = Object.assign(
        {
          numero: 1,
          imagem: 'BBBBBB',
        },
        new Pagina()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Pagina', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          numero: 1,
          texto: 'BBBBBB',
          planoDeAula: 'BBBBBB',
          imagem: 'BBBBBB',
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

    it('should delete a Pagina', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPaginaToCollectionIfMissing', () => {
      it('should add a Pagina to an empty array', () => {
        const pagina: IPagina = { id: 123 };
        expectedResult = service.addPaginaToCollectionIfMissing([], pagina);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pagina);
      });

      it('should not add a Pagina to an array that contains it', () => {
        const pagina: IPagina = { id: 123 };
        const paginaCollection: IPagina[] = [
          {
            ...pagina,
          },
          { id: 456 },
        ];
        expectedResult = service.addPaginaToCollectionIfMissing(paginaCollection, pagina);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Pagina to an array that doesn't contain it", () => {
        const pagina: IPagina = { id: 123 };
        const paginaCollection: IPagina[] = [{ id: 456 }];
        expectedResult = service.addPaginaToCollectionIfMissing(paginaCollection, pagina);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pagina);
      });

      it('should add only unique Pagina to an array', () => {
        const paginaArray: IPagina[] = [{ id: 123 }, { id: 456 }, { id: 36761 }];
        const paginaCollection: IPagina[] = [{ id: 123 }];
        expectedResult = service.addPaginaToCollectionIfMissing(paginaCollection, ...paginaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pagina: IPagina = { id: 123 };
        const pagina2: IPagina = { id: 456 };
        expectedResult = service.addPaginaToCollectionIfMissing([], pagina, pagina2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pagina);
        expect(expectedResult).toContain(pagina2);
      });

      it('should accept null and undefined values', () => {
        const pagina: IPagina = { id: 123 };
        expectedResult = service.addPaginaToCollectionIfMissing([], null, pagina, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pagina);
      });

      it('should return initial array if no Pagina is added', () => {
        const paginaCollection: IPagina[] = [{ id: 123 }];
        expectedResult = service.addPaginaToCollectionIfMissing(paginaCollection, undefined, null);
        expect(expectedResult).toEqual(paginaCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
