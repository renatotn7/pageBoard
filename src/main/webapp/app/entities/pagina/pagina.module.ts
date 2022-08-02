import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PaginaComponent } from './list/pagina.component';
import { PaginaDetailComponent } from './detail/pagina-detail.component';
import { PaginaUpdateComponent } from './update/pagina-update.component';
import { PaginaDeleteDialogComponent } from './delete/pagina-delete-dialog.component';
import { PaginaRoutingModule } from './route/pagina-routing.module';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AgGridModule } from 'ag-grid-angular';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
@NgModule({
  imports: [SharedModule, PaginaRoutingModule, FlexLayoutModule, CKEditorModule, AgGridModule],
  declarations: [PaginaComponent, PaginaDetailComponent, PaginaUpdateComponent, PaginaDeleteDialogComponent],
  entryComponents: [PaginaDeleteDialogComponent],
})
export class PaginaModule {}
