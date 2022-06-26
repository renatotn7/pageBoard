import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IEndereco, Endereco } from '../endereco.model';
import { EnderecoService } from '../service/endereco.service';
import { ICidade } from 'app/entities/cidade/cidade.model';
import { CidadeService } from 'app/entities/cidade/service/cidade.service';
import { IEstado } from 'app/entities/estado/estado.model';
import { EstadoService } from 'app/entities/estado/service/estado.service';
import { IPais } from 'app/entities/pais/pais.model';
import { PaisService } from 'app/entities/pais/service/pais.service';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { PessoaService } from 'app/entities/pessoa/service/pessoa.service';

@Component({
  selector: 'jhi-endereco-update',
  templateUrl: './endereco-update.component.html',
})
export class EnderecoUpdateComponent implements OnInit {
  isSaving = false;

  cidadesCollection: ICidade[] = [];
  estadosCollection: IEstado[] = [];
  paisCollection: IPais[] = [];
  pessoasSharedCollection: IPessoa[] = [];

  editForm = this.fb.group({
    id: [],
    logradouro: [],
    numero: [],
    complemento: [],
    bairro: [],
    cEP: [],
    cidade: [],
    estado: [],
    pais: [],
    pessoa: [],
  });

  constructor(
    protected enderecoService: EnderecoService,
    protected cidadeService: CidadeService,
    protected estadoService: EstadoService,
    protected paisService: PaisService,
    protected pessoaService: PessoaService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ endereco }) => {
      this.updateForm(endereco);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const endereco = this.createFromForm();
    if (endereco.id !== undefined) {
      this.subscribeToSaveResponse(this.enderecoService.update(endereco));
    } else {
      this.subscribeToSaveResponse(this.enderecoService.create(endereco));
    }
  }

  trackCidadeById(_index: number, item: ICidade): number {
    return item.id!;
  }

  trackEstadoById(_index: number, item: IEstado): number {
    return item.id!;
  }

  trackPaisById(_index: number, item: IPais): number {
    return item.id!;
  }

  trackPessoaById(_index: number, item: IPessoa): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEndereco>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(endereco: IEndereco): void {
    this.editForm.patchValue({
      id: endereco.id,
      logradouro: endereco.logradouro,
      numero: endereco.numero,
      complemento: endereco.complemento,
      bairro: endereco.bairro,
      cEP: endereco.cEP,
      cidade: endereco.cidade,
      estado: endereco.estado,
      pais: endereco.pais,
      pessoa: endereco.pessoa,
    });

    this.cidadesCollection = this.cidadeService.addCidadeToCollectionIfMissing(this.cidadesCollection, endereco.cidade);
    this.estadosCollection = this.estadoService.addEstadoToCollectionIfMissing(this.estadosCollection, endereco.estado);
    this.paisCollection = this.paisService.addPaisToCollectionIfMissing(this.paisCollection, endereco.pais);
    this.pessoasSharedCollection = this.pessoaService.addPessoaToCollectionIfMissing(this.pessoasSharedCollection, endereco.pessoa);
  }

  protected loadRelationshipsOptions(): void {
    this.cidadeService
      .query({ filter: 'endereco-is-null' })
      .pipe(map((res: HttpResponse<ICidade[]>) => res.body ?? []))
      .pipe(map((cidades: ICidade[]) => this.cidadeService.addCidadeToCollectionIfMissing(cidades, this.editForm.get('cidade')!.value)))
      .subscribe((cidades: ICidade[]) => (this.cidadesCollection = cidades));

    this.estadoService
      .query({ filter: 'endereco-is-null' })
      .pipe(map((res: HttpResponse<IEstado[]>) => res.body ?? []))
      .pipe(map((estados: IEstado[]) => this.estadoService.addEstadoToCollectionIfMissing(estados, this.editForm.get('estado')!.value)))
      .subscribe((estados: IEstado[]) => (this.estadosCollection = estados));

    this.paisService
      .query({ filter: 'endereco-is-null' })
      .pipe(map((res: HttpResponse<IPais[]>) => res.body ?? []))
      .pipe(map((pais: IPais[]) => this.paisService.addPaisToCollectionIfMissing(pais, this.editForm.get('pais')!.value)))
      .subscribe((pais: IPais[]) => (this.paisCollection = pais));

    this.pessoaService
      .query()
      .pipe(map((res: HttpResponse<IPessoa[]>) => res.body ?? []))
      .pipe(map((pessoas: IPessoa[]) => this.pessoaService.addPessoaToCollectionIfMissing(pessoas, this.editForm.get('pessoa')!.value)))
      .subscribe((pessoas: IPessoa[]) => (this.pessoasSharedCollection = pessoas));
  }

  protected createFromForm(): IEndereco {
    return {
      ...new Endereco(),
      id: this.editForm.get(['id'])!.value,
      logradouro: this.editForm.get(['logradouro'])!.value,
      numero: this.editForm.get(['numero'])!.value,
      complemento: this.editForm.get(['complemento'])!.value,
      bairro: this.editForm.get(['bairro'])!.value,
      cEP: this.editForm.get(['cEP'])!.value,
      cidade: this.editForm.get(['cidade'])!.value,
      estado: this.editForm.get(['estado'])!.value,
      pais: this.editForm.get(['pais'])!.value,
      pessoa: this.editForm.get(['pessoa'])!.value,
    };
  }
}
