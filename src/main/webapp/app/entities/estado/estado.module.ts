import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EstadoComponent } from './list/estado.component';
import { EstadoDetailComponent } from './detail/estado-detail.component';
import { EstadoUpdateComponent } from './update/estado-update.component';
import { EstadoDeleteDialogComponent } from './delete/estado-delete-dialog.component';
import { EstadoRoutingModule } from './route/estado-routing.module';

@NgModule({
  imports: [SharedModule, EstadoRoutingModule],
  declarations: [EstadoComponent, EstadoDetailComponent, EstadoUpdateComponent, EstadoDeleteDialogComponent],
  entryComponents: [EstadoDeleteDialogComponent],
})
export class EstadoModule {}
