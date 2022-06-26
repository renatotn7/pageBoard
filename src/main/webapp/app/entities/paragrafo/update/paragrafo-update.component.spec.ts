import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ParagrafoService } from '../service/paragrafo.service';
import { IParagrafo, Paragrafo } from '../paragrafo.model';
import { IPagina } from 'app/entities/pagina/pagina.model';
import { PaginaService } from 'app/entities/pagina/service/pagina.service';

import { ParagrafoUpdateComponent } from './paragrafo-update.component';

describe('Paragrafo Management Update Component', () => {
  let comp: ParagrafoUpdateComponent;
  let fixture: ComponentFixture<ParagrafoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let paragrafoService: ParagrafoService;
  let paginaService: PaginaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ParagrafoUpdateComponent],
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
      .overrideTemplate(ParagrafoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ParagrafoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    paragrafoService = TestBed.inject(ParagrafoService);
    paginaService = TestBed.inject(PaginaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Pagina query and add missing value', () => {
      const paragrafo: IParagrafo = { id: 456 };
      const pagina: IPagina = { id: 23486 };
      paragrafo.pagina = pagina;

      const paginaCollection: IPagina[] = [{ id: 18613 }];
      jest.spyOn(paginaService, 'query').mockReturnValue(of(new HttpResponse({ body: paginaCollection })));
      const additionalPaginas = [pagina];
      const expectedCollection: IPagina[] = [...additionalPaginas, ...paginaCollection];
      jest.spyOn(paginaService, 'addPaginaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ paragrafo });
      comp.ngOnInit();

      expect(paginaService.query).toHaveBeenCalled();
      expect(paginaService.addPaginaToCollectionIfMissing).toHaveBeenCalledWith(paginaCollection, ...additionalPaginas);
      expect(comp.paginasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const paragrafo: IParagrafo = { id: 456 };
      const pagina: IPagina = { id: 48534 };
      paragrafo.pagina = pagina;

      activatedRoute.data = of({ paragrafo });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(paragrafo));
      expect(comp.paginasSharedCollection).toContain(pagina);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Paragrafo>>();
      const paragrafo = { id: 123 };
      jest.spyOn(paragrafoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paragrafo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: paragrafo }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(paragrafoService.update).toHaveBeenCalledWith(paragrafo);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Paragrafo>>();
      const paragrafo = new Paragrafo();
      jest.spyOn(paragrafoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paragrafo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: paragrafo }));
      saveSubject.complete();

      // THEN
      expect(paragrafoService.create).toHaveBeenCalledWith(paragrafo);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Paragrafo>>();
      const paragrafo = { id: 123 };
      jest.spyOn(paragrafoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paragrafo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(paragrafoService.update).toHaveBeenCalledWith(paragrafo);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackPaginaById', () => {
      it('Should return tracked Pagina primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPaginaById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
