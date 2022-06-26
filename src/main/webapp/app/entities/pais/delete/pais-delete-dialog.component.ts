import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPais } from '../pais.model';
import { PaisService } from '../service/pais.service';

@Component({
  templateUrl: './pais-delete-dialog.component.html',
})
export class PaisDeleteDialogComponent {
  pais?: IPais;

  constructor(protected paisService: PaisService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.paisService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
