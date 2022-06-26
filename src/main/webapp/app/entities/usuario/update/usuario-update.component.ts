import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IUsuario, Usuario } from '../usuario.model';
import { UsuarioService } from '../service/usuario.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { PessoaService } from 'app/entities/pessoa/service/pessoa.service';

@Component({
  selector: 'jhi-usuario-update',
  templateUrl: './usuario-update.component.html',
})
export class UsuarioUpdateComponent implements OnInit {
  isSaving = false;

  pessoasCollection: IPessoa[] = [];

  editForm = this.fb.group({
    id: [],
    login: [],
    email: [],
    foto: [],
    fotoContentType: [],
    pessoa: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected usuarioService: UsuarioService,
    protected pessoaService: PessoaService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ usuario }) => {
      this.updateForm(usuario);

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

  save(): void {
    this.isSaving = true;
    const usuario = this.createFromForm();
    if (usuario.id !== undefined) {
      this.subscribeToSaveResponse(this.usuarioService.update(usuario));
    } else {
      this.subscribeToSaveResponse(this.usuarioService.create(usuario));
    }
  }

  trackPessoaById(_index: number, item: IPessoa): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUsuario>>): void {
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

  protected updateForm(usuario: IUsuario): void {
    this.editForm.patchValue({
      id: usuario.id,
      login: usuario.login,
      email: usuario.email,
      foto: usuario.foto,
      fotoContentType: usuario.fotoContentType,
      pessoa: usuario.pessoa,
    });

    this.pessoasCollection = this.pessoaService.addPessoaToCollectionIfMissing(this.pessoasCollection, usuario.pessoa);
  }

  protected loadRelationshipsOptions(): void {
    this.pessoaService
      .query({ filter: 'usuario-is-null' })
      .pipe(map((res: HttpResponse<IPessoa[]>) => res.body ?? []))
      .pipe(map((pessoas: IPessoa[]) => this.pessoaService.addPessoaToCollectionIfMissing(pessoas, this.editForm.get('pessoa')!.value)))
      .subscribe((pessoas: IPessoa[]) => (this.pessoasCollection = pessoas));
  }

  protected createFromForm(): IUsuario {
    return {
      ...new Usuario(),
      id: this.editForm.get(['id'])!.value,
      login: this.editForm.get(['login'])!.value,
      email: this.editForm.get(['email'])!.value,
      fotoContentType: this.editForm.get(['fotoContentType'])!.value,
      foto: this.editForm.get(['foto'])!.value,
      pessoa: this.editForm.get(['pessoa'])!.value,
    };
  }
}
