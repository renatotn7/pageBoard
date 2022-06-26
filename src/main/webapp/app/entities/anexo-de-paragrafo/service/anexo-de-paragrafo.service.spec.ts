import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { TipoAnexoDeParagrafo } from 'app/entities/enumerations/tipo-anexo-de-paragrafo.model';
import { IAnexoDeParagrafo, AnexoDeParagrafo } from '../anexo-de-paragrafo.model';

import { AnexoDeParagrafoService } from './anexo-de-paragrafo.service';

describe('AnexoDeParagrafo Service', () => {
  let service: AnexoDeParagrafoService;
  let httpMock: HttpTestingController;
  let elemDefault: IAnexoDeParagrafo;
  let expectedResult: IAnexoDeParagrafo | IAnexoDeParagrafo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AnexoDeParagrafoService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      tipo: TipoAnexoDeParagrafo.EXPLICACAO,
      value: 'AAAAAAA',
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

    it('should create a AnexoDeParagrafo', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new AnexoDeParagrafo()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AnexoDeParagrafo', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          tipo: 'BBBBBB',
          value: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AnexoDeParagrafo', () => {
      const patchObject = Object.assign(
        {
          tipo: 'BBBBBB',
          value: 'BBBBBB',
        },
        new AnexoDeParagrafo()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AnexoDeParagrafo', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          tipo: 'BBBBBB',
          value: 'BBBBBB',
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

    it('should delete a AnexoDeParagrafo', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAnexoDeParagrafoToCollectionIfMissing', () => {
      it('should add a AnexoDeParagrafo to an empty array', () => {
        const anexoDeParagrafo: IAnexoDeParagrafo = { id: 123 };
        expectedResult = service.addAnexoDeParagrafoToCollectionIfMissing([], anexoDeParagrafo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(anexoDeParagrafo);
      });

      it('should not add a AnexoDeParagrafo to an array that contains it', () => {
        const anexoDeParagrafo: IAnexoDeParagrafo = { id: 123 };
        const anexoDeParagrafoCollection: IAnexoDeParagrafo[] = [
          {
            ...anexoDeParagrafo,
          },
          { id: 456 },
        ];
        expectedResult = service.addAnexoDeParagrafoToCollectionIfMissing(anexoDeParagrafoCollection, anexoDeParagrafo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AnexoDeParagrafo to an array that doesn't contain it", () => {
        const anexoDeParagrafo: IAnexoDeParagrafo = { id: 123 };
        const anexoDeParagrafoCollection: IAnexoDeParagrafo[] = [{ id: 456 }];
        expectedResult = service.addAnexoDeParagrafoToCollectionIfMissing(anexoDeParagrafoCollection, anexoDeParagrafo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(anexoDeParagrafo);
      });

      it('should add only unique AnexoDeParagrafo to an array', () => {
        const anexoDeParagrafoArray: IAnexoDeParagrafo[] = [{ id: 123 }, { id: 456 }, { id: 51683 }];
        const anexoDeParagrafoCollection: IAnexoDeParagrafo[] = [{ id: 123 }];
        expectedResult = service.addAnexoDeParagrafoToCollectionIfMissing(anexoDeParagrafoCollection, ...anexoDeParagrafoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const anexoDeParagrafo: IAnexoDeParagrafo = { id: 123 };
        const anexoDeParagrafo2: IAnexoDeParagrafo = { id: 456 };
        expectedResult = service.addAnexoDeParagrafoToCollectionIfMissing([], anexoDeParagrafo, anexoDeParagrafo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(anexoDeParagrafo);
        expect(expectedResult).toContain(anexoDeParagrafo2);
      });

      it('should accept null and undefined values', () => {
        const anexoDeParagrafo: IAnexoDeParagrafo = { id: 123 };
        expectedResult = service.addAnexoDeParagrafoToCollectionIfMissing([], null, anexoDeParagrafo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(anexoDeParagrafo);
      });

      it('should return initial array if no AnexoDeParagrafo is added', () => {
        const anexoDeParagrafoCollection: IAnexoDeParagrafo[] = [{ id: 123 }];
        expectedResult = service.addAnexoDeParagrafoToCollectionIfMissing(anexoDeParagrafoCollection, undefined, null);
        expect(expectedResult).toEqual(anexoDeParagrafoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
