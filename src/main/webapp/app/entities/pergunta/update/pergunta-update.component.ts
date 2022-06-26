import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPergunta, Pergunta } from '../pergunta.model';
import { PerguntaService } from '../service/pergunta.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IParagrafo } from 'app/entities/paragrafo/paragrafo.model';
import { ParagrafoService } from 'app/entities/paragrafo/service/paragrafo.service';

@Component({
  selector: 'jhi-pergunta-update',
  templateUrl: './pergunta-update.component.html',
})
export class PerguntaUpdateComponent implements OnInit {
  isSaving = false;

  paragrafosSharedCollection: IParagrafo[] = [];

  editForm = this.fb.group({
    id: [],
    questao: [],
    resposta: [],
    paragrafo: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected perguntaService: PerguntaService,
    protected paragrafoService: ParagrafoService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pergunta }) => {
      this.updateForm(pergunta);

      this.loadRelationshipsOptions();
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
    const pergunta = this.createFromForm();
    if (pergunta.id !== undefined) {
      this.subscribeToSaveResponse(this.perguntaService.update(pergunta));
    } else {
      this.subscribeToSaveResponse(this.perguntaService.create(pergunta));
    }
  }

  trackParagrafoById(_index: number, item: IParagrafo): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPergunta>>): void {
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

  protected updateForm(pergunta: IPergunta): void {
    this.editForm.patchValue({
      id: pergunta.id,
      questao: pergunta.questao,
      resposta: pergunta.resposta,
      paragrafo: pergunta.paragrafo,
    });

    this.paragrafosSharedCollection = this.paragrafoService.addParagrafoToCollectionIfMissing(
      this.paragrafosSharedCollection,
      pergunta.paragrafo
    );
  }

  protected loadRelationshipsOptions(): void {
    this.paragrafoService
      .query()
      .pipe(map((res: HttpResponse<IParagrafo[]>) => res.body ?? []))
      .pipe(
        map((paragrafos: IParagrafo[]) =>
          this.paragrafoService.addParagrafoToCollectionIfMissing(paragrafos, this.editForm.get('paragrafo')!.value)
        )
      )
      .subscribe((paragrafos: IParagrafo[]) => (this.paragrafosSharedCollection = paragrafos));
  }

  protected createFromForm(): IPergunta {
    return {
      ...new Pergunta(),
      id: this.editForm.get(['id'])!.value,
      questao: this.editForm.get(['questao'])!.value,
      resposta: this.editForm.get(['resposta'])!.value,
      paragrafo: this.editForm.get(['paragrafo'])!.value,
    };
  }
}
