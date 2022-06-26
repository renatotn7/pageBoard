import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PageBoardService } from '../service/page-board.service';
import { IPageBoard, PageBoard } from '../page-board.model';

import { PageBoardUpdateComponent } from './page-board-update.component';

describe('PageBoard Management Update Component', () => {
  let comp: PageBoardUpdateComponent;
  let fixture: ComponentFixture<PageBoardUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let pageBoardService: PageBoardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PageBoardUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PageBoardUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PageBoardUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    pageBoardService = TestBed.inject(PageBoardService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const pageBoard: IPageBoard = { id: 456 };

      activatedRoute.data = of({ pageBoard });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(pageBoard));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PageBoard>>();
      const pageBoard = { id: 123 };
      jest.spyOn(pageBoardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pageBoard });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pageBoard }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(pageBoardService.update).toHaveBeenCalledWith(pageBoard);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PageBoard>>();
      const pageBoard = new PageBoard();
      jest.spyOn(pageBoardService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pageBoard });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pageBoard }));
      saveSubject.complete();

      // THEN
      expect(pageBoardService.create).toHaveBeenCalledWith(pageBoard);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PageBoard>>();
      const pageBoard = { id: 123 };
      jest.spyOn(pageBoardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pageBoard });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(pageBoardService.update).toHaveBeenCalledWith(pageBoard);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
