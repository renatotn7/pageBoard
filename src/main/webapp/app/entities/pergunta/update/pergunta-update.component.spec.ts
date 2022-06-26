import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PerguntaService } from '../service/pergunta.service';
import { IPergunta, Pergunta } from '../pergunta.model';
import { IParagrafo } from 'app/entities/paragrafo/paragrafo.model';
import { ParagrafoService } from 'app/entities/paragrafo/service/paragrafo.service';

import { PerguntaUpdateComponent } from './pergunta-update.component';

describe('Pergunta Management Update Component', () => {
  let comp: PerguntaUpdateComponent;
  let fixture: ComponentFixture<PerguntaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let perguntaService: PerguntaService;
  let paragrafoService: ParagrafoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PerguntaUpdateComponent],
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
      .overrideTemplate(PerguntaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PerguntaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    perguntaService = TestBed.inject(PerguntaService);
    paragrafoService = TestBed.inject(ParagrafoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Paragrafo query and add missing value', () => {
      const pergunta: IPergunta = { id: 456 };
      const paragrafo: IParagrafo = { id: 44416 };
      pergunta.paragrafo = paragrafo;

      const paragrafoCollection: IParagrafo[] = [{ id: 16939 }];
      jest.spyOn(paragrafoService, 'query').mockReturnValue(of(new HttpResponse({ body: paragrafoCollection })));
      const additionalParagrafos = [paragrafo];
      const expectedCollection: IParagrafo[] = [...additionalParagrafos, ...paragrafoCollection];
      jest.spyOn(paragrafoService, 'addParagrafoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pergunta });
      comp.ngOnInit();

      expect(paragrafoService.query).toHaveBeenCalled();
      expect(paragrafoService.addParagrafoToCollectionIfMissing).toHaveBeenCalledWith(paragrafoCollection, ...additionalParagrafos);
      expect(comp.paragrafosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const pergunta: IPergunta = { id: 456 };
      const paragrafo: IParagrafo = { id: 62912 };
      pergunta.paragrafo = paragrafo;

      activatedRoute.data = of({ pergunta });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(pergunta));
      expect(comp.paragrafosSharedCollection).toContain(paragrafo);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pergunta>>();
      const pergunta = { id: 123 };
      jest.spyOn(perguntaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pergunta });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pergunta }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(perguntaService.update).toHaveBeenCalledWith(pergunta);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pergunta>>();
      const pergunta = new Pergunta();
      jest.spyOn(perguntaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pergunta });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pergunta }));
      saveSubject.complete();

      // THEN
      expect(perguntaService.create).toHaveBeenCalledWith(pergunta);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pergunta>>();
      const pergunta = { id: 123 };
      jest.spyOn(perguntaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pergunta });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(perguntaService.update).toHaveBeenCalledWith(pergunta);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackParagrafoById', () => {
      it('Should return tracked Paragrafo primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackParagrafoById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
