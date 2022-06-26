import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ParagrafoComponent } from './list/paragrafo.component';
import { ParagrafoDetailComponent } from './detail/paragrafo-detail.component';
import { ParagrafoUpdateComponent } from './update/paragrafo-update.component';
import { ParagrafoDeleteDialogComponent } from './delete/paragrafo-delete-dialog.component';
import { ParagrafoRoutingModule } from './route/paragrafo-routing.module';

@NgModule({
  imports: [SharedModule, ParagrafoRoutingModule],
  declarations: [ParagrafoComponent, ParagrafoDetailComponent, ParagrafoUpdateComponent, ParagrafoDeleteDialogComponent],
  entryComponents: [ParagrafoDeleteDialogComponent],
})
export class ParagrafoModule {}
