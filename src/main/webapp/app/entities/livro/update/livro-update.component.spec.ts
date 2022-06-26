import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LivroService } from '../service/livro.service';
import { ILivro, Livro } from '../livro.model';
import { IProjeto } from 'app/entities/projeto/projeto.model';
import { ProjetoService } from 'app/entities/projeto/service/projeto.service';
import { IAssunto } from 'app/entities/assunto/assunto.model';
import { AssuntoService } from 'app/entities/assunto/service/assunto.service';

import { LivroUpdateComponent } from './livro-update.component';

describe('Livro Management Update Component', () => {
  let comp: LivroUpdateComponent;
  let fixture: ComponentFixture<LivroUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let livroService: LivroService;
  let projetoService: ProjetoService;
  let assuntoService: AssuntoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LivroUpdateComponent],
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
      .overrideTemplate(LivroUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LivroUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    livroService = TestBed.inject(LivroService);
    projetoService = TestBed.inject(ProjetoService);
    assuntoService = TestBed.inject(AssuntoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Projeto query and add missing value', () => {
      const livro: ILivro = { id: 456 };
      const projetos: IProjeto[] = [{ id: 89027 }];
      livro.projetos = projetos;

      const projetoCollection: IProjeto[] = [{ id: 4556 }];
      jest.spyOn(projetoService, 'query').mockReturnValue(of(new HttpResponse({ body: projetoCollection })));
      const additionalProjetos = [...projetos];
      const expectedCollection: IProjeto[] = [...additionalProjetos, ...projetoCollection];
      jest.spyOn(projetoService, 'addProjetoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ livro });
      comp.ngOnInit();

      expect(projetoService.query).toHaveBeenCalled();
      expect(projetoService.addProjetoToCollectionIfMissing).toHaveBeenCalledWith(projetoCollection, ...additionalProjetos);
      expect(comp.projetosSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Assunto query and add missing value', () => {
      const livro: ILivro = { id: 456 };
      const assunto: IAssunto = { id: 6123 };
      livro.assunto = assunto;

      const assuntoCollection: IAssunto[] = [{ id: 76107 }];
      jest.spyOn(assuntoService, 'query').mockReturnValue(of(new HttpResponse({ body: assuntoCollection })));
      const additionalAssuntos = [assunto];
      const expectedCollection: IAssunto[] = [...additionalAssuntos, ...assuntoCollection];
      jest.spyOn(assuntoService, 'addAssuntoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ livro });
      comp.ngOnInit();

      expect(assuntoService.query).toHaveBeenCalled();
      expect(assuntoService.addAssuntoToCollectionIfMissing).toHaveBeenCalledWith(assuntoCollection, ...additionalAssuntos);
      expect(comp.assuntosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const livro: ILivro = { id: 456 };
      const projetos: IProjeto = { id: 89845 };
      livro.projetos = [projetos];
      const assunto: IAssunto = { id: 61512 };
      livro.assunto = assunto;

      activatedRoute.data = of({ livro });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(livro));
      expect(comp.projetosSharedCollection).toContain(projetos);
      expect(comp.assuntosSharedCollection).toContain(assunto);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Livro>>();
      const livro = { id: 123 };
      jest.spyOn(livroService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ livro });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: livro }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(livroService.update).toHaveBeenCalledWith(livro);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Livro>>();
      const livro = new Livro();
      jest.spyOn(livroService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ livro });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: livro }));
      saveSubject.complete();

      // THEN
      expect(livroService.create).toHaveBeenCalledWith(livro);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Livro>>();
      const livro = { id: 123 };
      jest.spyOn(livroService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ livro });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(livroService.update).toHaveBeenCalledWith(livro);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackProjetoById', () => {
      it('Should return tracked Projeto primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProjetoById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackAssuntoById', () => {
      it('Should return tracked Assunto primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackAssuntoById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedProjeto', () => {
      it('Should return option if no Projeto is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedProjeto(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Projeto for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedProjeto(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Projeto is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedProjeto(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
