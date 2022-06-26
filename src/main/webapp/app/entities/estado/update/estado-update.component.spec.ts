import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EstadoService } from '../service/estado.service';
import { IEstado, Estado } from '../estado.model';
import { IPais } from 'app/entities/pais/pais.model';
import { PaisService } from 'app/entities/pais/service/pais.service';

import { EstadoUpdateComponent } from './estado-update.component';

describe('Estado Management Update Component', () => {
  let comp: EstadoUpdateComponent;
  let fixture: ComponentFixture<EstadoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let estadoService: EstadoService;
  let paisService: PaisService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EstadoUpdateComponent],
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
      .overrideTemplate(EstadoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EstadoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    estadoService = TestBed.inject(EstadoService);
    paisService = TestBed.inject(PaisService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call pais query and add missing value', () => {
      const estado: IEstado = { id: 456 };
      const pais: IPais = { id: 44273 };
      estado.pais = pais;

      const paisCollection: IPais[] = [{ id: 50340 }];
      jest.spyOn(paisService, 'query').mockReturnValue(of(new HttpResponse({ body: paisCollection })));
      const expectedCollection: IPais[] = [pais, ...paisCollection];
      jest.spyOn(paisService, 'addPaisToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ estado });
      comp.ngOnInit();

      expect(paisService.query).toHaveBeenCalled();
      expect(paisService.addPaisToCollectionIfMissing).toHaveBeenCalledWith(paisCollection, pais);
      expect(comp.paisCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const estado: IEstado = { id: 456 };
      const pais: IPais = { id: 14491 };
      estado.pais = pais;

      activatedRoute.data = of({ estado });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(estado));
      expect(comp.paisCollection).toContain(pais);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Estado>>();
      const estado = { id: 123 };
      jest.spyOn(estadoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ estado });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: estado }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(estadoService.update).toHaveBeenCalledWith(estado);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Estado>>();
      const estado = new Estado();
      jest.spyOn(estadoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ estado });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: estado }));
      saveSubject.complete();

      // THEN
      expect(estadoService.create).toHaveBeenCalledWith(estado);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Estado>>();
      const estado = { id: 123 };
      jest.spyOn(estadoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ estado });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(estadoService.update).toHaveBeenCalledWith(estado);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackPaisById', () => {
      it('Should return tracked Pais primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPaisById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
