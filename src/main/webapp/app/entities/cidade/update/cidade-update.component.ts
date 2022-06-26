import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICidade, Cidade } from '../cidade.model';
import { CidadeService } from '../service/cidade.service';
import { IEstado } from 'app/entities/estado/estado.model';
import { EstadoService } from 'app/entities/estado/service/estado.service';

@Component({
  selector: 'jhi-cidade-update',
  templateUrl: './cidade-update.component.html',
})
export class CidadeUpdateComponent implements OnInit {
  isSaving = false;

  estadosCollection: IEstado[] = [];

  editForm = this.fb.group({
    id: [],
    nome: [],
    estado: [],
  });

  constructor(
    protected cidadeService: CidadeService,
    protected estadoService: EstadoService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cidade }) => {
      this.updateForm(cidade);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cidade = this.createFromForm();
    if (cidade.id !== undefined) {
      this.subscribeToSaveResponse(this.cidadeService.update(cidade));
    } else {
      this.subscribeToSaveResponse(this.cidadeService.create(cidade));
    }
  }

  trackEstadoById(_index: number, item: IEstado): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICidade>>): void {
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

  protected updateForm(cidade: ICidade): void {
    this.editForm.patchValue({
      id: cidade.id,
      nome: cidade.nome,
      estado: cidade.estado,
    });

    this.estadosCollection = this.estadoService.addEstadoToCollectionIfMissing(this.estadosCollection, cidade.estado);
  }

  protected loadRelationshipsOptions(): void {
    this.estadoService
      .query({ filter: 'cidade-is-null' })
      .pipe(map((res: HttpResponse<IEstado[]>) => res.body ?? []))
      .pipe(map((estados: IEstado[]) => this.estadoService.addEstadoToCollectionIfMissing(estados, this.editForm.get('estado')!.value)))
      .subscribe((estados: IEstado[]) => (this.estadosCollection = estados));
  }

  protected createFromForm(): ICidade {
    return {
      ...new Cidade(),
      id: this.editForm.get(['id'])!.value,
      nome: this.editForm.get(['nome'])!.value,
      estado: this.editForm.get(['estado'])!.value,
    };
  }
}
