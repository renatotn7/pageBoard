import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProjeto } from '../projeto.model';
import { ProjetoService } from '../service/projeto.service';

@Component({
  templateUrl: './projeto-delete-dialog.component.html',
})
export class ProjetoDeleteDialogComponent {
  projeto?: IProjeto;

  constructor(protected projetoService: ProjetoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.projetoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
