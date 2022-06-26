import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IPais, Pais } from '../pais.model';
import { PaisService } from '../service/pais.service';

@Component({
  selector: 'jhi-pais-update',
  templateUrl: './pais-update.component.html',
})
export class PaisUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nome: [],
    sigla: [],
  });

  constructor(protected paisService: PaisService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pais }) => {
      this.updateForm(pais);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pais = this.createFromForm();
    if (pais.id !== undefined) {
      this.subscribeToSaveResponse(this.paisService.update(pais));
    } else {
      this.subscribeToSaveResponse(this.paisService.create(pais));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPais>>): void {
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

  protected updateForm(pais: IPais): void {
    this.editForm.patchValue({
      id: pais.id,
      nome: pais.nome,
      sigla: pais.sigla,
    });
  }

  protected createFromForm(): IPais {
    return {
      ...new Pais(),
      id: this.editForm.get(['id'])!.value,
      nome: this.editForm.get(['nome'])!.value,
      sigla: this.editForm.get(['sigla'])!.value,
    };
  }
}
