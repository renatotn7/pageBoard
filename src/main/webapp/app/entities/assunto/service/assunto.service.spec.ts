import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAssunto, Assunto } from '../assunto.model';

import { AssuntoService } from './assunto.service';

describe('Assunto Service', () => {
  let service: AssuntoService;
  let httpMock: HttpTestingController;
  let elemDefault: IAssunto;
  let expectedResult: IAssunto | IAssunto[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AssuntoService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nome: 'AAAAAAA',
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

    it('should create a Assunto', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Assunto()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Assunto', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nome: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Assunto', () => {
      const patchObject = Object.assign({}, new Assunto());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Assunto', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nome: 'BBBBBB',
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

    it('should delete a Assunto', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAssuntoToCollectionIfMissing', () => {
      it('should add a Assunto to an empty array', () => {
        const assunto: IAssunto = { id: 123 };
        expectedResult = service.addAssuntoToCollectionIfMissing([], assunto);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(assunto);
      });

      it('should not add a Assunto to an array that contains it', () => {
        const assunto: IAssunto = { id: 123 };
        const assuntoCollection: IAssunto[] = [
          {
            ...assunto,
          },
          { id: 456 },
        ];
        expectedResult = service.addAssuntoToCollectionIfMissing(assuntoCollection, assunto);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Assunto to an array that doesn't contain it", () => {
        const assunto: IAssunto = { id: 123 };
        const assuntoCollection: IAssunto[] = [{ id: 456 }];
        expectedResult = service.addAssuntoToCollectionIfMissing(assuntoCollection, assunto);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(assunto);
      });

      it('should add only unique Assunto to an array', () => {
        const assuntoArray: IAssunto[] = [{ id: 123 }, { id: 456 }, { id: 33458 }];
        const assuntoCollection: IAssunto[] = [{ id: 123 }];
        expectedResult = service.addAssuntoToCollectionIfMissing(assuntoCollection, ...assuntoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const assunto: IAssunto = { id: 123 };
        const assunto2: IAssunto = { id: 456 };
        expectedResult = service.addAssuntoToCollectionIfMissing([], assunto, assunto2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(assunto);
        expect(expectedResult).toContain(assunto2);
      });

      it('should accept null and undefined values', () => {
        const assunto: IAssunto = { id: 123 };
        expectedResult = service.addAssuntoToCollectionIfMissing([], null, assunto, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(assunto);
      });

      it('should return initial array if no Assunto is added', () => {
        const assuntoCollection: IAssunto[] = [{ id: 123 }];
        expectedResult = service.addAssuntoToCollectionIfMissing(assuntoCollection, undefined, null);
        expect(expectedResult).toEqual(assuntoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
