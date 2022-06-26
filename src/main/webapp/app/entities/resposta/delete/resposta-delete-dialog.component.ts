import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IResposta } from '../resposta.model';
import { RespostaService } from '../service/resposta.service';

@Component({
  templateUrl: './resposta-delete-dialog.component.html',
})
export class RespostaDeleteDialogComponent {
  resposta?: IResposta;

  constructor(protected respostaService: RespostaService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.respostaService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
