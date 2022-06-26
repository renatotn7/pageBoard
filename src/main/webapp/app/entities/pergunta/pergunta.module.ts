import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PerguntaComponent } from './list/pergunta.component';
import { PerguntaDetailComponent } from './detail/pergunta-detail.component';
import { PerguntaUpdateComponent } from './update/pergunta-update.component';
import { PerguntaDeleteDialogComponent } from './delete/pergunta-delete-dialog.component';
import { PerguntaRoutingModule } from './route/pergunta-routing.module';

@NgModule({
  imports: [SharedModule, PerguntaRoutingModule],
  declarations: [PerguntaComponent, PerguntaDetailComponent, PerguntaUpdateComponent, PerguntaDeleteDialogComponent],
  entryComponents: [PerguntaDeleteDialogComponent],
})
export class PerguntaModule {}
