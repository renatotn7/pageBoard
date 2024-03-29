import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPergunta } from '../pergunta.model';
import { PerguntaService } from '../service/pergunta.service';
import { PerguntaDeleteDialogComponent } from '../delete/pergunta-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-pergunta',
  templateUrl: './pergunta.component.html',
})
export class PerguntaComponent implements OnInit {
  perguntas?: IPergunta[];
  isLoading = false;

  constructor(protected perguntaService: PerguntaService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.perguntaService.query().subscribe({
      next: (res: HttpResponse<IPergunta[]>) => {
        this.isLoading = false;
        this.perguntas = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IPergunta): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(pergunta: IPergunta): void {
    const modalRef = this.modalService.open(PerguntaDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pergunta = pergunta;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
