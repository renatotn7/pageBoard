import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPais } from '../pais.model';
import { PaisService } from '../service/pais.service';
import { PaisDeleteDialogComponent } from '../delete/pais-delete-dialog.component';

@Component({
  selector: 'jhi-pais',
  templateUrl: './pais.component.html',
})
export class PaisComponent implements OnInit {
  pais?: IPais[];
  isLoading = false;

  constructor(protected paisService: PaisService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.paisService.query().subscribe({
      next: (res: HttpResponse<IPais[]>) => {
        this.isLoading = false;
        this.pais = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IPais): number {
    return item.id!;
  }

  delete(pais: IPais): void {
    const modalRef = this.modalService.open(PaisDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pais = pais;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
