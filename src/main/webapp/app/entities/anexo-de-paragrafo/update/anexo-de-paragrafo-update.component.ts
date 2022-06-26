import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IAnexoDeParagrafo, AnexoDeParagrafo } from '../anexo-de-paragrafo.model';
import { AnexoDeParagrafoService } from '../service/anexo-de-paragrafo.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IParagrafo } from 'app/entities/paragrafo/paragrafo.model';
import { ParagrafoService } from 'app/entities/paragrafo/service/paragrafo.service';
import { TipoAnexoDeParagrafo } from 'app/entities/enumerations/tipo-anexo-de-paragrafo.model';

@Component({
  selector: 'jhi-anexo-de-paragrafo-update',
  templateUrl: './anexo-de-paragrafo-update.component.html',
})
export class AnexoDeParagrafoUpdateComponent implements OnInit {
  isSaving = false;
  tipoAnexoDeParagrafoValues = Object.keys(TipoAnexoDeParagrafo);

  paragrafosSharedCollection: IParagrafo[] = [];

  editForm = this.fb.group({
    id: [],
    tipo: [],
    value: [],
    paragrafo: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected anexoDeParagrafoService: AnexoDeParagrafoService,
    protected paragrafoService: ParagrafoService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ anexoDeParagrafo }) => {
      this.updateForm(anexoDeParagrafo);

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
    const anexoDeParagrafo = this.createFromForm();
    if (anexoDeParagrafo.id !== undefined) {
      this.subscribeToSaveResponse(this.anexoDeParagrafoService.update(anexoDeParagrafo));
    } else {
      this.subscribeToSaveResponse(this.anexoDeParagrafoService.create(anexoDeParagrafo));
    }
  }

  trackParagrafoById(_index: number, item: IParagrafo): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAnexoDeParagrafo>>): void {
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

  protected updateForm(anexoDeParagrafo: IAnexoDeParagrafo): void {
    this.editForm.patchValue({
      id: anexoDeParagrafo.id,
      tipo: anexoDeParagrafo.tipo,
      value: anexoDeParagrafo.value,
      paragrafo: anexoDeParagrafo.paragrafo,
    });

    this.paragrafosSharedCollection = this.paragrafoService.addParagrafoToCollectionIfMissing(
      this.paragrafosSharedCollection,
      anexoDeParagrafo.paragrafo
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

  protected createFromForm(): IAnexoDeParagrafo {
    return {
      ...new AnexoDeParagrafo(),
      id: this.editForm.get(['id'])!.value,
      tipo: this.editForm.get(['tipo'])!.value,
      value: this.editForm.get(['value'])!.value,
      paragrafo: this.editForm.get(['paragrafo'])!.value,
    };
  }
}
