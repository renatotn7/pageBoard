import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IEstado, Estado } from '../estado.model';
import { EstadoService } from '../service/estado.service';
import { IPais } from 'app/entities/pais/pais.model';
import { PaisService } from 'app/entities/pais/service/pais.service';

@Component({
  selector: 'jhi-estado-update',
  templateUrl: './estado-update.component.html',
})
export class EstadoUpdateComponent implements OnInit {
  isSaving = false;

  paisCollection: IPais[] = [];

  editForm = this.fb.group({
    id: [],
    nome: [],
    uf: [],
    pais: [],
  });

  constructor(
    protected estadoService: EstadoService,
    protected paisService: PaisService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ estado }) => {
      this.updateForm(estado);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const estado = this.createFromForm();
    if (estado.id !== undefined) {
      this.subscribeToSaveResponse(this.estadoService.update(estado));
    } else {
      this.subscribeToSaveResponse(this.estadoService.create(estado));
    }
  }

  trackPaisById(_index: number, item: IPais): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEstado>>): void {
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

  protected updateForm(estado: IEstado): void {
    this.editForm.patchValue({
      id: estado.id,
      nome: estado.nome,
      uf: estado.uf,
      pais: estado.pais,
    });

    this.paisCollection = this.paisService.addPaisToCollectionIfMissing(this.paisCollection, estado.pais);
  }

  protected loadRelationshipsOptions(): void {
    this.paisService
      .query({ filter: 'estado-is-null' })
      .pipe(map((res: HttpResponse<IPais[]>) => res.body ?? []))
      .pipe(map((pais: IPais[]) => this.paisService.addPaisToCollectionIfMissing(pais, this.editForm.get('pais')!.value)))
      .subscribe((pais: IPais[]) => (this.paisCollection = pais));
  }

  protected createFromForm(): IEstado {
    return {
      ...new Estado(),
      id: this.editForm.get(['id'])!.value,
      nome: this.editForm.get(['nome'])!.value,
      uf: this.editForm.get(['uf'])!.value,
      pais: this.editForm.get(['pais'])!.value,
    };
  }
}
