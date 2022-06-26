import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EnderecoService } from '../service/endereco.service';
import { IEndereco, Endereco } from '../endereco.model';
import { ICidade } from 'app/entities/cidade/cidade.model';
import { CidadeService } from 'app/entities/cidade/service/cidade.service';
import { IEstado } from 'app/entities/estado/estado.model';
import { EstadoService } from 'app/entities/estado/service/estado.service';
import { IPais } from 'app/entities/pais/pais.model';
import { PaisService } from 'app/entities/pais/service/pais.service';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { PessoaService } from 'app/entities/pessoa/service/pessoa.service';

import { EnderecoUpdateComponent } from './endereco-update.component';

describe('Endereco Management Update Component', () => {
  let comp: EnderecoUpdateComponent;
  let fixture: ComponentFixture<EnderecoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let enderecoService: EnderecoService;
  let cidadeService: CidadeService;
  let estadoService: EstadoService;
  let paisService: PaisService;
  let pessoaService: PessoaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EnderecoUpdateComponent],
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
      .overrideTemplate(EnderecoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EnderecoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    enderecoService = TestBed.inject(EnderecoService);
    cidadeService = TestBed.inject(CidadeService);
    estadoService = TestBed.inject(EstadoService);
    paisService = TestBed.inject(PaisService);
    pessoaService = TestBed.inject(PessoaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call cidade query and add missing value', () => {
      const endereco: IEndereco = { id: 456 };
      const cidade: ICidade = { id: 9982 };
      endereco.cidade = cidade;

      const cidadeCollection: ICidade[] = [{ id: 30196 }];
      jest.spyOn(cidadeService, 'query').mockReturnValue(of(new HttpResponse({ body: cidadeCollection })));
      const expectedCollection: ICidade[] = [cidade, ...cidadeCollection];
      jest.spyOn(cidadeService, 'addCidadeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ endereco });
      comp.ngOnInit();

      expect(cidadeService.query).toHaveBeenCalled();
      expect(cidadeService.addCidadeToCollectionIfMissing).toHaveBeenCalledWith(cidadeCollection, cidade);
      expect(comp.cidadesCollection).toEqual(expectedCollection);
    });

    it('Should call estado query and add missing value', () => {
      const endereco: IEndereco = { id: 456 };
      const estado: IEstado = { id: 34037 };
      endereco.estado = estado;

      const estadoCollection: IEstado[] = [{ id: 26307 }];
      jest.spyOn(estadoService, 'query').mockReturnValue(of(new HttpResponse({ body: estadoCollection })));
      const expectedCollection: IEstado[] = [estado, ...estadoCollection];
      jest.spyOn(estadoService, 'addEstadoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ endereco });
      comp.ngOnInit();

      expect(estadoService.query).toHaveBeenCalled();
      expect(estadoService.addEstadoToCollectionIfMissing).toHaveBeenCalledWith(estadoCollection, estado);
      expect(comp.estadosCollection).toEqual(expectedCollection);
    });

    it('Should call pais query and add missing value', () => {
      const endereco: IEndereco = { id: 456 };
      const pais: IPais = { id: 63255 };
      endereco.pais = pais;

      const paisCollection: IPais[] = [{ id: 35821 }];
      jest.spyOn(paisService, 'query').mockReturnValue(of(new HttpResponse({ body: paisCollection })));
      const expectedCollection: IPais[] = [pais, ...paisCollection];
      jest.spyOn(paisService, 'addPaisToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ endereco });
      comp.ngOnInit();

      expect(paisService.query).toHaveBeenCalled();
      expect(paisService.addPaisToCollectionIfMissing).toHaveBeenCalledWith(paisCollection, pais);
      expect(comp.paisCollection).toEqual(expectedCollection);
    });

    it('Should call Pessoa query and add missing value', () => {
      const endereco: IEndereco = { id: 456 };
      const pessoa: IPessoa = { id: 1294 };
      endereco.pessoa = pessoa;

      const pessoaCollection: IPessoa[] = [{ id: 35005 }];
      jest.spyOn(pessoaService, 'query').mockReturnValue(of(new HttpResponse({ body: pessoaCollection })));
      const additionalPessoas = [pessoa];
      const expectedCollection: IPessoa[] = [...additionalPessoas, ...pessoaCollection];
      jest.spyOn(pessoaService, 'addPessoaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ endereco });
      comp.ngOnInit();

      expect(pessoaService.query).toHaveBeenCalled();
      expect(pessoaService.addPessoaToCollectionIfMissing).toHaveBeenCalledWith(pessoaCollection, ...additionalPessoas);
      expect(comp.pessoasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const endereco: IEndereco = { id: 456 };
      const cidade: ICidade = { id: 7650 };
      endereco.cidade = cidade;
      const estado: IEstado = { id: 74131 };
      endereco.estado = estado;
      const pais: IPais = { id: 86429 };
      endereco.pais = pais;
      const pessoa: IPessoa = { id: 55986 };
      endereco.pessoa = pessoa;

      activatedRoute.data = of({ endereco });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(endereco));
      expect(comp.cidadesCollection).toContain(cidade);
      expect(comp.estadosCollection).toContain(estado);
      expect(comp.paisCollection).toContain(pais);
      expect(comp.pessoasSharedCollection).toContain(pessoa);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Endereco>>();
      const endereco = { id: 123 };
      jest.spyOn(enderecoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ endereco });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: endereco }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(enderecoService.update).toHaveBeenCalledWith(endereco);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Endereco>>();
      const endereco = new Endereco();
      jest.spyOn(enderecoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ endereco });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: endereco }));
      saveSubject.complete();

      // THEN
      expect(enderecoService.create).toHaveBeenCalledWith(endereco);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Endereco>>();
      const endereco = { id: 123 };
      jest.spyOn(enderecoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ endereco });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(enderecoService.update).toHaveBeenCalledWith(endereco);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackCidadeById', () => {
      it('Should return tracked Cidade primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCidadeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackEstadoById', () => {
      it('Should return tracked Estado primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackEstadoById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackPaisById', () => {
      it('Should return tracked Pais primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPaisById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackPessoaById', () => {
      it('Should return tracked Pessoa primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPessoaById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
