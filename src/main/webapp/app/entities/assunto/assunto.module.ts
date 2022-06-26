import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AssuntoComponent } from './list/assunto.component';
import { AssuntoDetailComponent } from './detail/assunto-detail.component';
import { AssuntoUpdateComponent } from './update/assunto-update.component';
import { AssuntoDeleteDialogComponent } from './delete/assunto-delete-dialog.component';
import { AssuntoRoutingModule } from './route/assunto-routing.module';

@NgModule({
  imports: [SharedModule, AssuntoRoutingModule],
  declarations: [AssuntoComponent, AssuntoDetailComponent, AssuntoUpdateComponent, AssuntoDeleteDialogComponent],
  entryComponents: [AssuntoDeleteDialogComponent],
})
export class AssuntoModule {}
