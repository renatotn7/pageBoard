import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RespostaService } from '../service/resposta.service';
import { IResposta, Resposta } from '../resposta.model';

import { RespostaUpdateComponent } from './resposta-update.component';

describe('Resposta Management Update Component', () => {
  let comp: RespostaUpdateComponent;
  let fixture: ComponentFixture<RespostaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let respostaService: RespostaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RespostaUpdateComponent],
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
      .overrideTemplate(RespostaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RespostaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    respostaService = TestBed.inject(RespostaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const resposta: IResposta = { id: 456 };

      activatedRoute.data = of({ resposta });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(resposta));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Resposta>>();
      const resposta = { id: 123 };
      jest.spyOn(respostaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resposta });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resposta }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(respostaService.update).toHaveBeenCalledWith(resposta);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Resposta>>();
      const resposta = new Resposta();
      jest.spyOn(respostaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resposta });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resposta }));
      saveSubject.complete();

      // THEN
      expect(respostaService.create).toHaveBeenCalledWith(resposta);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Resposta>>();
      const resposta = { id: 123 };
      jest.spyOn(respostaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resposta });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(respostaService.update).toHaveBeenCalledWith(resposta);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
