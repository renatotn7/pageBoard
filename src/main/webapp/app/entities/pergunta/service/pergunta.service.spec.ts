import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPergunta, Pergunta } from '../pergunta.model';

import { PerguntaService } from './pergunta.service';

describe('Pergunta Service', () => {
  let service: PerguntaService;
  let httpMock: HttpTestingController;
  let elemDefault: IPergunta;
  let expectedResult: IPergunta | IPergunta[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PerguntaService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      questao: 'AAAAAAA',
      resposta: 'AAAAAAA',
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

    it('should create a Pergunta', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Pergunta()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Pergunta', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          questao: 'BBBBBB',
          resposta: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Pergunta', () => {
      const patchObject = Object.assign(
        {
          questao: 'BBBBBB',
          resposta: 'BBBBBB',
        },
        new Pergunta()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Pergunta', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          questao: 'BBBBBB',
          resposta: 'BBBBBB',
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

    it('should delete a Pergunta', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPerguntaToCollectionIfMissing', () => {
      it('should add a Pergunta to an empty array', () => {
        const pergunta: IPergunta = { id: 123 };
        expectedResult = service.addPerguntaToCollectionIfMissing([], pergunta);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pergunta);
      });

      it('should not add a Pergunta to an array that contains it', () => {
        const pergunta: IPergunta = { id: 123 };
        const perguntaCollection: IPergunta[] = [
          {
            ...pergunta,
          },
          { id: 456 },
        ];
        expectedResult = service.addPerguntaToCollectionIfMissing(perguntaCollection, pergunta);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Pergunta to an array that doesn't contain it", () => {
        const pergunta: IPergunta = { id: 123 };
        const perguntaCollection: IPergunta[] = [{ id: 456 }];
        expectedResult = service.addPerguntaToCollectionIfMissing(perguntaCollection, pergunta);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pergunta);
      });

      it('should add only unique Pergunta to an array', () => {
        const perguntaArray: IPergunta[] = [{ id: 123 }, { id: 456 }, { id: 29334 }];
        const perguntaCollection: IPergunta[] = [{ id: 123 }];
        expectedResult = service.addPerguntaToCollectionIfMissing(perguntaCollection, ...perguntaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pergunta: IPergunta = { id: 123 };
        const pergunta2: IPergunta = { id: 456 };
        expectedResult = service.addPerguntaToCollectionIfMissing([], pergunta, pergunta2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pergunta);
        expect(expectedResult).toContain(pergunta2);
      });

      it('should accept null and undefined values', () => {
        const pergunta: IPergunta = { id: 123 };
        expectedResult = service.addPerguntaToCollectionIfMissing([], null, pergunta, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pergunta);
      });

      it('should return initial array if no Pergunta is added', () => {
        const perguntaCollection: IPergunta[] = [{ id: 123 }];
        expectedResult = service.addPerguntaToCollectionIfMissing(perguntaCollection, undefined, null);
        expect(expectedResult).toEqual(perguntaCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
