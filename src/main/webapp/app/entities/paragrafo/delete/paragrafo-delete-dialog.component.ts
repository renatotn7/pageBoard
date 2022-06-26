import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IParagrafo } from '../paragrafo.model';
import { ParagrafoService } from '../service/paragrafo.service';

@Component({
  templateUrl: './paragrafo-delete-dialog.component.html',
})
export class ParagrafoDeleteDialogComponent {
  paragrafo?: IParagrafo;

  constructor(protected paragrafoService: ParagrafoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.paragrafoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
