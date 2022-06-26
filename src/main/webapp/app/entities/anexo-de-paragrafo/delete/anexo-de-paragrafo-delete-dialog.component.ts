import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAnexoDeParagrafo } from '../anexo-de-paragrafo.model';
import { AnexoDeParagrafoService } from '../service/anexo-de-paragrafo.service';

@Component({
  templateUrl: './anexo-de-paragrafo-delete-dialog.component.html',
})
export class AnexoDeParagrafoDeleteDialogComponent {
  anexoDeParagrafo?: IAnexoDeParagrafo;

  constructor(protected anexoDeParagrafoService: AnexoDeParagrafoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.anexoDeParagrafoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
