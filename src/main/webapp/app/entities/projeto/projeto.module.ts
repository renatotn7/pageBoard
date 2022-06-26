import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProjetoComponent } from './list/projeto.component';
import { ProjetoDetailComponent } from './detail/projeto-detail.component';
import { ProjetoUpdateComponent } from './update/projeto-update.component';
import { ProjetoDeleteDialogComponent } from './delete/projeto-delete-dialog.component';
import { ProjetoRoutingModule } from './route/projeto-routing.module';

@NgModule({
  imports: [SharedModule, ProjetoRoutingModule],
  declarations: [ProjetoComponent, ProjetoDetailComponent, ProjetoUpdateComponent, ProjetoDeleteDialogComponent],
  entryComponents: [ProjetoDeleteDialogComponent],
})
export class ProjetoModule {}
