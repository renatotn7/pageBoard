import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IProjeto } from '../projeto.model';
import { ProjetoService } from '../service/projeto.service';
import { ProjetoDeleteDialogComponent } from '../delete/projeto-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-projeto',
  templateUrl: './projeto.component.html',
})
export class ProjetoComponent implements OnInit {
  projetos?: IProjeto[];
  isLoading = false;

  constructor(protected projetoService: ProjetoService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.projetoService.query().subscribe({
      next: (res: HttpResponse<IProjeto[]>) => {
        this.isLoading = false;
        this.projetos = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IProjeto): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(projeto: IProjeto): void {
    const modalRef = this.modalService.open(ProjetoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.projeto = projeto;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
