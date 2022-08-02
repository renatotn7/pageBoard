import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LivroComponent } from './list/livro.component';
import { LivroDetailComponent } from './detail/livro-detail.component';
import { LivroUpdateComponent } from './update/livro-update.component';
import { LivroDeleteDialogComponent } from './delete/livro-delete-dialog.component';
import { LivroRoutingModule } from './route/livro-routing.module';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ChipsModule } from 'primeng/chips';
import { RadioButtonModule } from 'primeng/radiobutton';
@NgModule({
  imports: [SharedModule, LivroRoutingModule, FlexLayoutModule, ChipsModule, RadioButtonModule],
  declarations: [LivroComponent, LivroDetailComponent, LivroUpdateComponent, LivroDeleteDialogComponent],
  entryComponents: [LivroDeleteDialogComponent],
})
export class LivroModule {}
