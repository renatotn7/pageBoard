import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPessoa } from '../pessoa.model';
import { PessoaService } from '../service/pessoa.service';
import { PessoaDeleteDialogComponent } from '../delete/pessoa-delete-dialog.component';

@Component({
  selector: 'jhi-pessoa',
  templateUrl: './pessoa.component.html',
})
export class PessoaComponent implements OnInit {
  pessoas?: IPessoa[];
  isLoading = false;

  constructor(protected pessoaService: PessoaService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.pessoaService.query().subscribe({
      next: (res: HttpResponse<IPessoa[]>) => {
        this.isLoading = false;
        this.pessoas = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IPessoa): number {
    return item.id!;
  }

  delete(pessoa: IPessoa): void {
    const modalRef = this.modalService.open(PessoaDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pessoa = pessoa;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
