import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AssuntoService } from '../service/assunto.service';
import { IAssunto, Assunto } from '../assunto.model';

import { AssuntoUpdateComponent } from './assunto-update.component';

describe('Assunto Management Update Component', () => {
  let comp: AssuntoUpdateComponent;
  let fixture: ComponentFixture<AssuntoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let assuntoService: AssuntoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AssuntoUpdateComponent],
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
      .overrideTemplate(AssuntoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AssuntoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    assuntoService = TestBed.inject(AssuntoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const assunto: IAssunto = { id: 456 };

      activatedRoute.data = of({ assunto });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(assunto));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Assunto>>();
      const assunto = { id: 123 };
      jest.spyOn(assuntoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ assunto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: assunto }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(assuntoService.update).toHaveBeenCalledWith(assunto);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Assunto>>();
      const assunto = new Assunto();
      jest.spyOn(assuntoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ assunto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: assunto }));
      saveSubject.complete();

      // THEN
      expect(assuntoService.create).toHaveBeenCalledWith(assunto);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Assunto>>();
      const assunto = { id: 123 };
      jest.spyOn(assuntoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ assunto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(assuntoService.update).toHaveBeenCalledWith(assunto);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
