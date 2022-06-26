import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AnexoDeParagrafoComponent } from './list/anexo-de-paragrafo.component';
import { AnexoDeParagrafoDetailComponent } from './detail/anexo-de-paragrafo-detail.component';
import { AnexoDeParagrafoUpdateComponent } from './update/anexo-de-paragrafo-update.component';
import { AnexoDeParagrafoDeleteDialogComponent } from './delete/anexo-de-paragrafo-delete-dialog.component';
import { AnexoDeParagrafoRoutingModule } from './route/anexo-de-paragrafo-routing.module';

@NgModule({
  imports: [SharedModule, AnexoDeParagrafoRoutingModule],
  declarations: [
    AnexoDeParagrafoComponent,
    AnexoDeParagrafoDetailComponent,
    AnexoDeParagrafoUpdateComponent,
    AnexoDeParagrafoDeleteDialogComponent,
  ],
  entryComponents: [AnexoDeParagrafoDeleteDialogComponent],
})
export class AnexoDeParagrafoModule {}
