import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AnexoDeParagrafoService } from '../service/anexo-de-paragrafo.service';
import { IAnexoDeParagrafo, AnexoDeParagrafo } from '../anexo-de-paragrafo.model';
import { IParagrafo } from 'app/entities/paragrafo/paragrafo.model';
import { ParagrafoService } from 'app/entities/paragrafo/service/paragrafo.service';

import { AnexoDeParagrafoUpdateComponent } from './anexo-de-paragrafo-update.component';

describe('AnexoDeParagrafo Management Update Component', () => {
  let comp: AnexoDeParagrafoUpdateComponent;
  let fixture: ComponentFixture<AnexoDeParagrafoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let anexoDeParagrafoService: AnexoDeParagrafoService;
  let paragrafoService: ParagrafoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AnexoDeParagrafoUpdateComponent],
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
      .overrideTemplate(AnexoDeParagrafoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AnexoDeParagrafoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    anexoDeParagrafoService = TestBed.inject(AnexoDeParagrafoService);
    paragrafoService = TestBed.inject(ParagrafoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Paragrafo query and add missing value', () => {
      const anexoDeParagrafo: IAnexoDeParagrafo = { id: 456 };
      const paragrafo: IParagrafo = { id: 56952 };
      anexoDeParagrafo.paragrafo = paragrafo;

      const paragrafoCollection: IParagrafo[] = [{ id: 40033 }];
      jest.spyOn(paragrafoService, 'query').mockReturnValue(of(new HttpResponse({ body: paragrafoCollection })));
      const additionalParagrafos = [paragrafo];
      const expectedCollection: IParagrafo[] = [...additionalParagrafos, ...paragrafoCollection];
      jest.spyOn(paragrafoService, 'addParagrafoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ anexoDeParagrafo });
      comp.ngOnInit();

      expect(paragrafoService.query).toHaveBeenCalled();
      expect(paragrafoService.addParagrafoToCollectionIfMissing).toHaveBeenCalledWith(paragrafoCollection, ...additionalParagrafos);
      expect(comp.paragrafosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const anexoDeParagrafo: IAnexoDeParagrafo = { id: 456 };
      const paragrafo: IParagrafo = { id: 21511 };
      anexoDeParagrafo.paragrafo = paragrafo;

      activatedRoute.data = of({ anexoDeParagrafo });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(anexoDeParagrafo));
      expect(comp.paragrafosSharedCollection).toContain(paragrafo);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AnexoDeParagrafo>>();
      const anexoDeParagrafo = { id: 123 };
      jest.spyOn(anexoDeParagrafoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ anexoDeParagrafo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: anexoDeParagrafo }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(anexoDeParagrafoService.update).toHaveBeenCalledWith(anexoDeParagrafo);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AnexoDeParagrafo>>();
      const anexoDeParagrafo = new AnexoDeParagrafo();
      jest.spyOn(anexoDeParagrafoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ anexoDeParagrafo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: anexoDeParagrafo }));
      saveSubject.complete();

      // THEN
      expect(anexoDeParagrafoService.create).toHaveBeenCalledWith(anexoDeParagrafo);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AnexoDeParagrafo>>();
      const anexoDeParagrafo = { id: 123 };
      jest.spyOn(anexoDeParagrafoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ anexoDeParagrafo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(anexoDeParagrafoService.update).toHaveBeenCalledWith(anexoDeParagrafo);
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
