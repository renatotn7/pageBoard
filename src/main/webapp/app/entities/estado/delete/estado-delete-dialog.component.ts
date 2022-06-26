import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEstado } from '../estado.model';
import { EstadoService } from '../service/estado.service';

@Component({
  templateUrl: './estado-delete-dialog.component.html',
})
export class EstadoDeleteDialogComponent {
  estado?: IEstado;

  constructor(protected estadoService: EstadoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.estadoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
