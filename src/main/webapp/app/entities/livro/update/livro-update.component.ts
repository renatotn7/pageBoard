import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILivro, Livro } from '../livro.model';
import { LivroService } from '../service/livro.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IProjeto } from 'app/entities/projeto/projeto.model';
import { ProjetoService } from 'app/entities/projeto/service/projeto.service';
import { IAssunto } from 'app/entities/assunto/assunto.model';
import { AssuntoService } from 'app/entities/assunto/service/assunto.service';
import {MenuItem} from 'primeng/api';
@Component({
  selector: 'jhi-livro-update',
  templateUrl: './livro-update.component.html',
  styleUrls: ['./livro-update.component.scss'],
})
export class LivroUpdateComponent implements OnInit {
  isSaving = false;

  projetosSharedCollection: IProjeto[] = [];
  assuntosSharedCollection: IAssunto[] = [];
  values1: string[];
  livro:Livro;

  editForm = this.fb.group({
    id: [],
    nomeLivro: [],
    editora: [],
    autor: [],
    anoDePublicacao: [],
    tags: [],
    projetos: [],
    assunto: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected livroService: LivroService,
    protected projetoService: ProjetoService,
    protected assuntoService: AssuntoService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {

    this.livro={}
    this.values1=[]
  }

  ngOnInit(): void {
   
    this.activatedRoute.data.subscribe(({ livro }) => {
      this.livro=livro;
    
    //  console.log( '')
      this.updateForm(livro);

      this.loadRelationshipsOptions();
      this.values1=this.livro.tags!.split(';')
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('pageBoardApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
  
    this.isSaving = true;
     const livro:ILivro = this.createFromForm();
     livro.tags= this.values1.join(";");
    if (livro.id !== undefined) {
      this.subscribeToSaveResponse(this.livroService.update(livro));
    } else {
      this.subscribeToSaveResponse(this.livroService.create(livro));
    }
  }

  trackProjetoById(_index: number, item: IProjeto): number {
    return item.id!;
  }

  trackAssuntoById(_index: number, item: IAssunto): number {
    return item.id!;
  }

  getSelectedProjeto(option: IProjeto, selectedVals?: IProjeto[]): IProjeto {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILivro>>): void {
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

  protected updateForm(livro: ILivro): void {
    this.editForm.patchValue({
      id: livro.id,
      nomeLivro: livro.nomeLivro,
      editora: livro.editora,
      autor: livro.autor,
      anoDePublicacao: livro.anoDePublicacao,
      tags: livro.tags,
      projetos: livro.projetos,
      assunto: livro.assunto,
    });

    this.projetosSharedCollection = this.projetoService.addProjetoToCollectionIfMissing(
      this.projetosSharedCollection,
      ...(livro.projetos ?? [])
    );
    this.assuntosSharedCollection = this.assuntoService.addAssuntoToCollectionIfMissing(this.assuntosSharedCollection, livro.assunto);
  }

  protected loadRelationshipsOptions(): void {
    this.projetoService
      .query()
      .pipe(map((res: HttpResponse<IProjeto[]>) => res.body ?? []))
      .pipe(
        map((projetos: IProjeto[]) =>
          this.projetoService.addProjetoToCollectionIfMissing(projetos, ...(this.editForm.get('projetos')!.value ?? []))
        )
      )
      .subscribe((projetos: IProjeto[]) => (this.projetosSharedCollection = projetos));

    this.assuntoService
      .query()
      .pipe(map((res: HttpResponse<IAssunto[]>) => res.body ?? []))
      .pipe(
        map((assuntos: IAssunto[]) => this.assuntoService.addAssuntoToCollectionIfMissing(assuntos, this.editForm.get('assunto')!.value))
      )
      .subscribe((assuntos: IAssunto[]) => (this.assuntosSharedCollection = assuntos));
  }

  protected createFromForm(): ILivro {
    return {
      ...new Livro(),
      id: this.editForm.get(['id'])!.value,
      nomeLivro: this.editForm.get(['nomeLivro'])!.value,
      editora: this.editForm.get(['editora'])!.value,
      autor: this.editForm.get(['autor'])!.value,
      anoDePublicacao: this.editForm.get(['anoDePublicacao'])!.value,
      tags: this.editForm.get(['tags'])!.value,
      projetos: this.editForm.get(['projetos'])!.value,
      assunto: this.editForm.get(['assunto'])!.value,
    };
  }
}
