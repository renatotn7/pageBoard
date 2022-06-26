import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IParagrafo, Paragrafo } from '../paragrafo.model';
import { ParagrafoService } from '../service/paragrafo.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IPagina } from 'app/entities/pagina/pagina.model';
import { PaginaService } from 'app/entities/pagina/service/pagina.service';

@Component({
  selector: 'jhi-paragrafo-update',
  templateUrl: './paragrafo-update.component.html',
})
export class ParagrafoUpdateComponent implements OnInit {
  isSaving = false;

  paginasSharedCollection: IPagina[] = [];

  editForm = this.fb.group({
    id: [],
    numero: [],
    texto: [],
    resumo: [],
    pagina: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected paragrafoService: ParagrafoService,
    protected paginaService: PaginaService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paragrafo }) => {
      this.updateForm(paragrafo);

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
    const paragrafo = this.createFromForm();
    if (paragrafo.id !== undefined) {
      this.subscribeToSaveResponse(this.paragrafoService.update(paragrafo));
    } else {
      this.subscribeToSaveResponse(this.paragrafoService.create(paragrafo));
    }
  }

  trackPaginaById(_index: number, item: IPagina): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IParagrafo>>): void {
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

  protected updateForm(paragrafo: IParagrafo): void {
    this.editForm.patchValue({
      id: paragrafo.id,
      numero: paragrafo.numero,
      texto: paragrafo.texto,
      resumo: paragrafo.resumo,
      pagina: paragrafo.pagina,
    });

    this.paginasSharedCollection = this.paginaService.addPaginaToCollectionIfMissing(this.paginasSharedCollection, paragrafo.pagina);
  }

  protected loadRelationshipsOptions(): void {
    this.paginaService
      .query()
      .pipe(map((res: HttpResponse<IPagina[]>) => res.body ?? []))
      .pipe(map((paginas: IPagina[]) => this.paginaService.addPaginaToCollectionIfMissing(paginas, this.editForm.get('pagina')!.value)))
      .subscribe((paginas: IPagina[]) => (this.paginasSharedCollection = paginas));
  }

  protected createFromForm(): IParagrafo {
    return {
      ...new Paragrafo(),
      id: this.editForm.get(['id'])!.value,
      numero: this.editForm.get(['numero'])!.value,
      texto: this.editForm.get(['texto'])!.value,
      resumo: this.editForm.get(['resumo'])!.value,
      pagina: this.editForm.get(['pagina'])!.value,
    };
  }
}
