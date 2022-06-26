import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAssunto } from '../assunto.model';
import { AssuntoService } from '../service/assunto.service';

@Component({
  templateUrl: './assunto-delete-dialog.component.html',
})
export class AssuntoDeleteDialogComponent {
  assunto?: IAssunto;

  constructor(protected assuntoService: AssuntoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.assuntoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
