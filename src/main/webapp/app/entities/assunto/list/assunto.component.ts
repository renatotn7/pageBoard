import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAssunto } from '../assunto.model';
import { AssuntoService } from '../service/assunto.service';
import { AssuntoDeleteDialogComponent } from '../delete/assunto-delete-dialog.component';

@Component({
  selector: 'jhi-assunto',
  templateUrl: './assunto.component.html',
})
export class AssuntoComponent implements OnInit {
  assuntos?: IAssunto[];
  isLoading = false;

  constructor(protected assuntoService: AssuntoService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.assuntoService.query().subscribe({
      next: (res: HttpResponse<IAssunto[]>) => {
        this.isLoading = false;
        this.assuntos = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IAssunto): number {
    return item.id!;
  }

  delete(assunto: IAssunto): void {
    const modalRef = this.modalService.open(AssuntoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.assunto = assunto;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
