import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPagina } from '../pagina.model';
import { PaginaService } from '../service/pagina.service';

@Component({
  templateUrl: './pagina-delete-dialog.component.html',
})
export class PaginaDeleteDialogComponent {
  pagina?: IPagina;

  constructor(protected paginaService: PaginaService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.paginaService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
