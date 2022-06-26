import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PageBoardComponent } from './list/page-board.component';
import { PageBoardDetailComponent } from './detail/page-board-detail.component';
import { PageBoardUpdateComponent } from './update/page-board-update.component';
import { PageBoardDeleteDialogComponent } from './delete/page-board-delete-dialog.component';
import { PageBoardRoutingModule } from './route/page-board-routing.module';

@NgModule({
  imports: [SharedModule, PageBoardRoutingModule],
  declarations: [PageBoardComponent, PageBoardDetailComponent, PageBoardUpdateComponent, PageBoardDeleteDialogComponent],
  entryComponents: [PageBoardDeleteDialogComponent],
})
export class PageBoardModule {}
