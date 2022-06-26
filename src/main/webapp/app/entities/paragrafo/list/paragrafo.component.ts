import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IParagrafo } from '../paragrafo.model';
import { ParagrafoService } from '../service/paragrafo.service';
import { ParagrafoDeleteDialogComponent } from '../delete/paragrafo-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-paragrafo',
  templateUrl: './paragrafo.component.html',
})
export class ParagrafoComponent implements OnInit {
  paragrafos?: IParagrafo[];
  isLoading = false;

  constructor(protected paragrafoService: ParagrafoService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.paragrafoService.query().subscribe({
      next: (res: HttpResponse<IParagrafo[]>) => {
        this.isLoading = false;
        this.paragrafos = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IParagrafo): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(paragrafo: IParagrafo): void {
    const modalRef = this.modalService.open(ParagrafoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.paragrafo = paragrafo;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
