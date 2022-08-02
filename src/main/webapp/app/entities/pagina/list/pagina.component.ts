import { Component, OnInit, ViewChild } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPagina } from '../pagina.model';
import { PaginaService } from '../service/pagina.service';
import { PaginaDeleteDialogComponent } from '../delete/pagina-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';


@Component({
  selector: 'jhi-pagina',
  templateUrl: './pagina.component.html',
  styleUrls: ['./pagina.component.scss']
})
export class PaginaComponent implements OnInit {
  paginas?: IPagina[];
  isLoading = false;

  constructor(protected paginaService: PaginaService, protected dataUtils: DataUtils, protected modalService: NgbModal,
    ) {}

  loadAll(): void {
    this.isLoading = true;

    this.paginaService.query().subscribe({
      next: (res: HttpResponse<IPagina[]>) => {
        this.isLoading = false;
        this.paginas = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IPagina): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(pagina: IPagina): void {
    const modalRef = this.modalService.open(PaginaDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pagina = pagina;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
  
}
