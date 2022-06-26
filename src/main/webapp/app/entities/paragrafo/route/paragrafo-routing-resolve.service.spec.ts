import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IParagrafo, Paragrafo } from '../paragrafo.model';
import { ParagrafoService } from '../service/paragrafo.service';

import { ParagrafoRoutingResolveService } from './paragrafo-routing-resolve.service';

describe('Paragrafo routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ParagrafoRoutingResolveService;
  let service: ParagrafoService;
  let resultParagrafo: IParagrafo | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(ParagrafoRoutingResolveService);
    service = TestBed.inject(ParagrafoService);
    resultParagrafo = undefined;
  });

  describe('resolve', () => {
    it('should return IParagrafo returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultParagrafo = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultParagrafo).toEqual({ id: 123 });
    });

    it('should return new IParagrafo if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultParagrafo = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultParagrafo).toEqual(new Paragrafo());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Paragrafo })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultParagrafo = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultParagrafo).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
