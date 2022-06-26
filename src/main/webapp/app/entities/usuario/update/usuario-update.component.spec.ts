import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UsuarioService } from '../service/usuario.service';
import { IUsuario, Usuario } from '../usuario.model';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { PessoaService } from 'app/entities/pessoa/service/pessoa.service';

import { UsuarioUpdateComponent } from './usuario-update.component';

describe('Usuario Management Update Component', () => {
  let comp: UsuarioUpdateComponent;
  let fixture: ComponentFixture<UsuarioUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let usuarioService: UsuarioService;
  let pessoaService: PessoaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UsuarioUpdateComponent],
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
      .overrideTemplate(UsuarioUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UsuarioUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    usuarioService = TestBed.inject(UsuarioService);
    pessoaService = TestBed.inject(PessoaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call pessoa query and add missing value', () => {
      const usuario: IUsuario = { id: 456 };
      const pessoa: IPessoa = { id: 28784 };
      usuario.pessoa = pessoa;

      const pessoaCollection: IPessoa[] = [{ id: 83908 }];
      jest.spyOn(pessoaService, 'query').mockReturnValue(of(new HttpResponse({ body: pessoaCollection })));
      const expectedCollection: IPessoa[] = [pessoa, ...pessoaCollection];
      jest.spyOn(pessoaService, 'addPessoaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ usuario });
      comp.ngOnInit();

      expect(pessoaService.query).toHaveBeenCalled();
      expect(pessoaService.addPessoaToCollectionIfMissing).toHaveBeenCalledWith(pessoaCollection, pessoa);
      expect(comp.pessoasCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const usuario: IUsuario = { id: 456 };
      const pessoa: IPessoa = { id: 92803 };
      usuario.pessoa = pessoa;

      activatedRoute.data = of({ usuario });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(usuario));
      expect(comp.pessoasCollection).toContain(pessoa);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Usuario>>();
      const usuario = { id: 123 };
      jest.spyOn(usuarioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ usuario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: usuario }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(usuarioService.update).toHaveBeenCalledWith(usuario);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Usuario>>();
      const usuario = new Usuario();
      jest.spyOn(usuarioService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ usuario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: usuario }));
      saveSubject.complete();

      // THEN
      expect(usuarioService.create).toHaveBeenCalledWith(usuario);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Usuario>>();
      const usuario = { id: 123 };
      jest.spyOn(usuarioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ usuario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(usuarioService.update).toHaveBeenCalledWith(usuario);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackPessoaById', () => {
      it('Should return tracked Pessoa primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPessoaById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
