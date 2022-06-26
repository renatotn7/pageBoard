import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPergunta } from '../pergunta.model';
import { PerguntaService } from '../service/pergunta.service';

@Component({
  templateUrl: './pergunta-delete-dialog.component.html',
})
export class PerguntaDeleteDialogComponent {
  pergunta?: IPergunta;

  constructor(protected perguntaService: PerguntaService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.perguntaService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
