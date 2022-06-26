import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEstado, Estado } from '../estado.model';

import { EstadoService } from './estado.service';

describe('Estado Service', () => {
  let service: EstadoService;
  let httpMock: HttpTestingController;
  let elemDefault: IEstado;
  let expectedResult: IEstado | IEstado[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EstadoService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nome: 'AAAAAAA',
      uf: 'AAAAAAA',
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

    it('should create a Estado', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Estado()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Estado', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nome: 'BBBBBB',
          uf: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Estado', () => {
      const patchObject = Object.assign(
        {
          nome: 'BBBBBB',
        },
        new Estado()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Estado', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nome: 'BBBBBB',
          uf: 'BBBBBB',
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

    it('should delete a Estado', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEstadoToCollectionIfMissing', () => {
      it('should add a Estado to an empty array', () => {
        const estado: IEstado = { id: 123 };
        expectedResult = service.addEstadoToCollectionIfMissing([], estado);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(estado);
      });

      it('should not add a Estado to an array that contains it', () => {
        const estado: IEstado = { id: 123 };
        const estadoCollection: IEstado[] = [
          {
            ...estado,
          },
          { id: 456 },
        ];
        expectedResult = service.addEstadoToCollectionIfMissing(estadoCollection, estado);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Estado to an array that doesn't contain it", () => {
        const estado: IEstado = { id: 123 };
        const estadoCollection: IEstado[] = [{ id: 456 }];
        expectedResult = service.addEstadoToCollectionIfMissing(estadoCollection, estado);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(estado);
      });

      it('should add only unique Estado to an array', () => {
        const estadoArray: IEstado[] = [{ id: 123 }, { id: 456 }, { id: 7852 }];
        const estadoCollection: IEstado[] = [{ id: 123 }];
        expectedResult = service.addEstadoToCollectionIfMissing(estadoCollection, ...estadoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const estado: IEstado = { id: 123 };
        const estado2: IEstado = { id: 456 };
        expectedResult = service.addEstadoToCollectionIfMissing([], estado, estado2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(estado);
        expect(expectedResult).toContain(estado2);
      });

      it('should accept null and undefined values', () => {
        const estado: IEstado = { id: 123 };
        expectedResult = service.addEstadoToCollectionIfMissing([], null, estado, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(estado);
      });

      it('should return initial array if no Estado is added', () => {
        const estadoCollection: IEstado[] = [{ id: 123 }];
        expectedResult = service.addEstadoToCollectionIfMissing(estadoCollection, undefined, null);
        expect(expectedResult).toEqual(estadoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
