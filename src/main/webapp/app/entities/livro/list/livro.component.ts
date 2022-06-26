import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILivro } from '../livro.model';
import { LivroService } from '../service/livro.service';
import { LivroDeleteDialogComponent } from '../delete/livro-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-livro',
  templateUrl: './livro.component.html',
})
export class LivroComponent implements OnInit {
  livros?: ILivro[];
  isLoading = false;

  constructor(protected livroService: LivroService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.livroService.query().subscribe({
      next: (res: HttpResponse<ILivro[]>) => {
        this.isLoading = false;
        this.livros = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ILivro): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(livro: ILivro): void {
    const modalRef = this.modalService.open(LivroDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.livro = livro;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
