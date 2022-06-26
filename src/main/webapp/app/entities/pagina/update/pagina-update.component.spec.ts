import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PaginaService } from '../service/pagina.service';
import { IPagina, Pagina } from '../pagina.model';
import { ILivro } from 'app/entities/livro/livro.model';
import { LivroService } from 'app/entities/livro/service/livro.service';

import { PaginaUpdateComponent } from './pagina-update.component';

describe('Pagina Management Update Component', () => {
  let comp: PaginaUpdateComponent;
  let fixture: ComponentFixture<PaginaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let paginaService: PaginaService;
  let livroService: LivroService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PaginaUpdateComponent],
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
      .overrideTemplate(PaginaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PaginaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    paginaService = TestBed.inject(PaginaService);
    livroService = TestBed.inject(LivroService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Livro query and add missing value', () => {
      const pagina: IPagina = { id: 456 };
      const livro: ILivro = { id: 96846 };
      pagina.livro = livro;

      const livroCollection: ILivro[] = [{ id: 16966 }];
      jest.spyOn(livroService, 'query').mockReturnValue(of(new HttpResponse({ body: livroCollection })));
      const additionalLivros = [livro];
      const expectedCollection: ILivro[] = [...additionalLivros, ...livroCollection];
      jest.spyOn(livroService, 'addLivroToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pagina });
      comp.ngOnInit();

      expect(livroService.query).toHaveBeenCalled();
      expect(livroService.addLivroToCollectionIfMissing).toHaveBeenCalledWith(livroCollection, ...additionalLivros);
      expect(comp.livrosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const pagina: IPagina = { id: 456 };
      const livro: ILivro = { id: 20299 };
      pagina.livro = livro;

      activatedRoute.data = of({ pagina });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(pagina));
      expect(comp.livrosSharedCollection).toContain(livro);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pagina>>();
      const pagina = { id: 123 };
      jest.spyOn(paginaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pagina });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pagina }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(paginaService.update).toHaveBeenCalledWith(pagina);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pagina>>();
      const pagina = new Pagina();
      jest.spyOn(paginaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pagina });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pagina }));
      saveSubject.complete();

      // THEN
      expect(paginaService.create).toHaveBeenCalledWith(pagina);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pagina>>();
      const pagina = { id: 123 };
      jest.spyOn(paginaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pagina });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(paginaService.update).toHaveBeenCalledWith(pagina);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackLivroById', () => {
      it('Should return tracked Livro primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackLivroById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
