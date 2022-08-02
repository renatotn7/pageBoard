import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ILivro } from '../livro.model';
import { DataUtils } from 'app/core/util/data-util.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LivroDeleteDialogComponent } from '../delete/livro-delete-dialog.component';
@Component({
  selector: 'jhi-livro-detail',
  templateUrl: './livro-detail.component.html',
})
export class LivroDetailComponent implements OnInit {
  livro: ILivro | null = null;

  constructor(
    protected dataUtils: DataUtils,
    protected activatedRoute: ActivatedRoute,
    protected modalService: NgbModal,
    protected router: Router
  ) {}

  ngOnInit(): void {
    // alert(this.activatedRoute.url.)

    this.activatedRoute.data.subscribe(({ livro }) => {
      this.livro = livro;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }
  delete(livro: ILivro): void {
    const modalRef = this.modalService.open(LivroDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.livro = livro;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.router.navigate(['../../'], { relativeTo: this.activatedRoute });
      }
    });
  }
  previousState(): void {
    window.history.back();
  }
}
