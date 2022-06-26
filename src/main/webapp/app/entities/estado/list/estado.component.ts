import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IEstado } from '../estado.model';
import { EstadoService } from '../service/estado.service';
import { EstadoDeleteDialogComponent } from '../delete/estado-delete-dialog.component';

@Component({
  selector: 'jhi-estado',
  templateUrl: './estado.component.html',
})
export class EstadoComponent implements OnInit {
  estados?: IEstado[];
  isLoading = false;

  constructor(protected estadoService: EstadoService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.estadoService.query().subscribe({
      next: (res: HttpResponse<IEstado[]>) => {
        this.isLoading = false;
        this.estados = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IEstado): number {
    return item.id!;
  }

  delete(estado: IEstado): void {
    const modalRef = this.modalService.open(EstadoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.estado = estado;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
