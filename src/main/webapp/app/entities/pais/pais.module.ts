import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PaisComponent } from './list/pais.component';
import { PaisDetailComponent } from './detail/pais-detail.component';
import { PaisUpdateComponent } from './update/pais-update.component';
import { PaisDeleteDialogComponent } from './delete/pais-delete-dialog.component';
import { PaisRoutingModule } from './route/pais-routing.module';

@NgModule({
  imports: [SharedModule, PaisRoutingModule],
  declarations: [PaisComponent, PaisDetailComponent, PaisUpdateComponent, PaisDeleteDialogComponent],
  entryComponents: [PaisDeleteDialogComponent],
})
export class PaisModule {}
