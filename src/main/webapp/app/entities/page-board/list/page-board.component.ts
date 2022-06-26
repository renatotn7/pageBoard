import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPageBoard } from '../page-board.model';
import { PageBoardService } from '../service/page-board.service';
import { PageBoardDeleteDialogComponent } from '../delete/page-board-delete-dialog.component';

@Component({
  selector: 'jhi-page-board',
  templateUrl: './page-board.component.html',
})
export class PageBoardComponent implements OnInit {
  pageBoards?: IPageBoard[];
  isLoading = false;

  constructor(protected pageBoardService: PageBoardService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.pageBoardService.query().subscribe({
      next: (res: HttpResponse<IPageBoard[]>) => {
        this.isLoading = false;
        this.pageBoards = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IPageBoard): number {
    return item.id!;
  }

  delete(pageBoard: IPageBoard): void {
    const modalRef = this.modalService.open(PageBoardDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pageBoard = pageBoard;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
