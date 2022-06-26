import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPageBoard, PageBoard } from '../page-board.model';

import { PageBoardService } from './page-board.service';

describe('PageBoard Service', () => {
  let service: PageBoardService;
  let httpMock: HttpTestingController;
  let elemDefault: IPageBoard;
  let expectedResult: IPageBoard | IPageBoard[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PageBoardService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
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

    it('should create a PageBoard', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new PageBoard()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PageBoard', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PageBoard', () => {
      const patchObject = Object.assign({}, new PageBoard());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PageBoard', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
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

    it('should delete a PageBoard', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPageBoardToCollectionIfMissing', () => {
      it('should add a PageBoard to an empty array', () => {
        const pageBoard: IPageBoard = { id: 123 };
        expectedResult = service.addPageBoardToCollectionIfMissing([], pageBoard);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pageBoard);
      });

      it('should not add a PageBoard to an array that contains it', () => {
        const pageBoard: IPageBoard = { id: 123 };
        const pageBoardCollection: IPageBoard[] = [
          {
            ...pageBoard,
          },
          { id: 456 },
        ];
        expectedResult = service.addPageBoardToCollectionIfMissing(pageBoardCollection, pageBoard);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PageBoard to an array that doesn't contain it", () => {
        const pageBoard: IPageBoard = { id: 123 };
        const pageBoardCollection: IPageBoard[] = [{ id: 456 }];
        expectedResult = service.addPageBoardToCollectionIfMissing(pageBoardCollection, pageBoard);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pageBoard);
      });

      it('should add only unique PageBoard to an array', () => {
        const pageBoardArray: IPageBoard[] = [{ id: 123 }, { id: 456 }, { id: 20165 }];
        const pageBoardCollection: IPageBoard[] = [{ id: 123 }];
        expectedResult = service.addPageBoardToCollectionIfMissing(pageBoardCollection, ...pageBoardArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pageBoard: IPageBoard = { id: 123 };
        const pageBoard2: IPageBoard = { id: 456 };
        expectedResult = service.addPageBoardToCollectionIfMissing([], pageBoard, pageBoard2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pageBoard);
        expect(expectedResult).toContain(pageBoard2);
      });

      it('should accept null and undefined values', () => {
        const pageBoard: IPageBoard = { id: 123 };
        expectedResult = service.addPageBoardToCollectionIfMissing([], null, pageBoard, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pageBoard);
      });

      it('should return initial array if no PageBoard is added', () => {
        const pageBoardCollection: IPageBoard[] = [{ id: 123 }];
        expectedResult = service.addPageBoardToCollectionIfMissing(pageBoardCollection, undefined, null);
        expect(expectedResult).toEqual(pageBoardCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
