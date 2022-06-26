import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILivro } from '../livro.model';
import { LivroService } from '../service/livro.service';

@Component({
  templateUrl: './livro-delete-dialog.component.html',
})
export class LivroDeleteDialogComponent {
  livro?: ILivro;

  constructor(protected livroService: LivroService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.livroService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
