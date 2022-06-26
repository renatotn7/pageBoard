import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RespostaComponent } from './list/resposta.component';
import { RespostaDetailComponent } from './detail/resposta-detail.component';
import { RespostaUpdateComponent } from './update/resposta-update.component';
import { RespostaDeleteDialogComponent } from './delete/resposta-delete-dialog.component';
import { RespostaRoutingModule } from './route/resposta-routing.module';

@NgModule({
  imports: [SharedModule, RespostaRoutingModule],
  declarations: [RespostaComponent, RespostaDetailComponent, RespostaUpdateComponent, RespostaDeleteDialogComponent],
  entryComponents: [RespostaDeleteDialogComponent],
})
export class RespostaModule {}
