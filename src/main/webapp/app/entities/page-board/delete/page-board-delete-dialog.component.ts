import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPageBoard } from '../page-board.model';
import { PageBoardService } from '../service/page-board.service';

@Component({
  templateUrl: './page-board-delete-dialog.component.html',
})
export class PageBoardDeleteDialogComponent {
  pageBoard?: IPageBoard;

  constructor(protected pageBoardService: PageBoardService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.pageBoardService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
