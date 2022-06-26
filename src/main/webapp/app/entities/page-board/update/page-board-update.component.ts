import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IPageBoard, PageBoard } from '../page-board.model';
import { PageBoardService } from '../service/page-board.service';

@Component({
  selector: 'jhi-page-board-update',
  templateUrl: './page-board-update.component.html',
})
export class PageBoardUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
  });

  constructor(protected pageBoardService: PageBoardService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pageBoard }) => {
      this.updateForm(pageBoard);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pageBoard = this.createFromForm();
    if (pageBoard.id !== undefined) {
      this.subscribeToSaveResponse(this.pageBoardService.update(pageBoard));
    } else {
      this.subscribeToSaveResponse(this.pageBoardService.create(pageBoard));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPageBoard>>): void {
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

  protected updateForm(pageBoard: IPageBoard): void {
    this.editForm.patchValue({
      id: pageBoard.id,
    });
  }

  protected createFromForm(): IPageBoard {
    return {
      ...new PageBoard(),
      id: this.editForm.get(['id'])!.value,
    };
  }
}
