import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPagina, Pagina } from '../pagina.model';
import { PaginaService } from '../service/pagina.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ILivro } from 'app/entities/livro/livro.model';
import { LivroService } from 'app/entities/livro/service/livro.service';
import * as ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import { ChangeEvent } from '@ckeditor/ckeditor5-angular';

@Component({
  selector: 'jhi-pagina-update',
  templateUrl: './pagina-update.component.html',
  styleUrls: ['./pagina-update.component.scss'],
})
export class PaginaUpdateComponent implements OnInit {
  isSaving = false;
  public Editor = ClassicEditor;
  public editorData = '<p>Hello, world!</p>';
  livrosSharedCollection: ILivro[] = [];
  text!: string;
  public model = {
    editorData: '<p>Hello, world!</p>',
  };

  editForm = this.fb.group({
    id: [],
    numero: [],
    texto: [],
    planoDeAula: [],
    imagem: [],
    imagemContentType: [],
    livro: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected paginaService: PaginaService,
    protected livroService: LivroService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pagina }) => {
      this.updateForm(pagina);
      this.model.editorData = pagina.texto;
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

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }
  public onChangeEditor({ editor }: ChangeEvent): void {
    const data = editor.getData();
    this.model.editorData = data;
  }
  save(): void {
    this.isSaving = true;
    const pagina = this.createFromForm();

    if (pagina.id !== undefined) {
      pagina.texto = this.model.editorData;
      this.subscribeToSaveResponse(this.paginaService.update(pagina));
    } else {
      this.subscribeToSaveResponse(this.paginaService.create(pagina));
    }
  }

  trackLivroById(_index: number, item: ILivro): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPagina>>): void {
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

  protected updateForm(pagina: IPagina): void {
    this.editForm.patchValue({
      id: pagina.id,
      numero: pagina.numero,
      texto: pagina.texto,
      planoDeAula: pagina.planoDeAula,
      imagem: pagina.imagem,
      imagemContentType: pagina.imagemContentType,
      livro: pagina.livro,
    });

    this.livrosSharedCollection = this.livroService.addLivroToCollectionIfMissing(this.livrosSharedCollection, pagina.livro);
  }

  protected loadRelationshipsOptions(): void {
    this.livroService
      .query()
      .pipe(map((res: HttpResponse<ILivro[]>) => res.body ?? []))
      .pipe(map((livros: ILivro[]) => this.livroService.addLivroToCollectionIfMissing(livros, this.editForm.get('livro')!.value)))
      .subscribe((livros: ILivro[]) => (this.livrosSharedCollection = livros));
  }

  protected createFromForm(): IPagina {
    return {
      ...new Pagina(),
      id: this.editForm.get(['id'])!.value,
      numero: this.editForm.get(['numero'])!.value,
      texto: this.editForm.get(['texto'])!.value,
      planoDeAula: this.editForm.get(['planoDeAula'])!.value,
      imagemContentType: this.editForm.get(['imagemContentType'])!.value,
      imagem: this.editForm.get(['imagem'])!.value,
      livro: this.editForm.get(['livro'])!.value,
    };
  }
}
