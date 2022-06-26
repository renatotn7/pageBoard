import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IPessoa, Pessoa } from '../pessoa.model';
import { PessoaService } from '../service/pessoa.service';

@Component({
  selector: 'jhi-pessoa-update',
  templateUrl: './pessoa-update.component.html',
})
export class PessoaUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nome: [],
    dataDeNascimento: [],
  });

  constructor(protected pessoaService: PessoaService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pessoa }) => {
      this.updateForm(pessoa);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pessoa = this.createFromForm();
    if (pessoa.id !== undefined) {
      this.subscribeToSaveResponse(this.pessoaService.update(pessoa));
    } else {
      this.subscribeToSaveResponse(this.pessoaService.create(pessoa));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPessoa>>): void {
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

  protected updateForm(pessoa: IPessoa): void {
    this.editForm.patchValue({
      id: pessoa.id,
      nome: pessoa.nome,
      dataDeNascimento: pessoa.dataDeNascimento,
    });
  }

  protected createFromForm(): IPessoa {
    return {
      ...new Pessoa(),
      id: this.editForm.get(['id'])!.value,
      nome: this.editForm.get(['nome'])!.value,
      dataDeNascimento: this.editForm.get(['dataDeNascimento'])!.value,
    };
  }
}
