import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IParagrafo, Paragrafo } from '../paragrafo.model';

import { ParagrafoService } from './paragrafo.service';

describe('Paragrafo Service', () => {
  let service: ParagrafoService;
  let httpMock: HttpTestingController;
  let elemDefault: IParagrafo;
  let expectedResult: IParagrafo | IParagrafo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ParagrafoService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      numero: 0,
      texto: 'AAAAAAA',
      resumo: 'AAAAAAA',
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

    it('should create a Paragrafo', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Paragrafo()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Paragrafo', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          numero: 1,
          texto: 'BBBBBB',
          resumo: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Paragrafo', () => {
      const patchObject = Object.assign(
        {
          numero: 1,
          texto: 'BBBBBB',
          resumo: 'BBBBBB',
        },
        new Paragrafo()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Paragrafo', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          numero: 1,
          texto: 'BBBBBB',
          resumo: 'BBBBBB',
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

    it('should delete a Paragrafo', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addParagrafoToCollectionIfMissing', () => {
      it('should add a Paragrafo to an empty array', () => {
        const paragrafo: IParagrafo = { id: 123 };
        expectedResult = service.addParagrafoToCollectionIfMissing([], paragrafo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(paragrafo);
      });

      it('should not add a Paragrafo to an array that contains it', () => {
        const paragrafo: IParagrafo = { id: 123 };
        const paragrafoCollection: IParagrafo[] = [
          {
            ...paragrafo,
          },
          { id: 456 },
        ];
        expectedResult = service.addParagrafoToCollectionIfMissing(paragrafoCollection, paragrafo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Paragrafo to an array that doesn't contain it", () => {
        const paragrafo: IParagrafo = { id: 123 };
        const paragrafoCollection: IParagrafo[] = [{ id: 456 }];
        expectedResult = service.addParagrafoToCollectionIfMissing(paragrafoCollection, paragrafo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(paragrafo);
      });

      it('should add only unique Paragrafo to an array', () => {
        const paragrafoArray: IParagrafo[] = [{ id: 123 }, { id: 456 }, { id: 49395 }];
        const paragrafoCollection: IParagrafo[] = [{ id: 123 }];
        expectedResult = service.addParagrafoToCollectionIfMissing(paragrafoCollection, ...paragrafoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const paragrafo: IParagrafo = { id: 123 };
        const paragrafo2: IParagrafo = { id: 456 };
        expectedResult = service.addParagrafoToCollectionIfMissing([], paragrafo, paragrafo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(paragrafo);
        expect(expectedResult).toContain(paragrafo2);
      });

      it('should accept null and undefined values', () => {
        const paragrafo: IParagrafo = { id: 123 };
        expectedResult = service.addParagrafoToCollectionIfMissing([], null, paragrafo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(paragrafo);
      });

      it('should return initial array if no Paragrafo is added', () => {
        const paragrafoCollection: IParagrafo[] = [{ id: 123 }];
        expectedResult = service.addParagrafoToCollectionIfMissing(paragrafoCollection, undefined, null);
        expect(expectedResult).toEqual(paragrafoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
