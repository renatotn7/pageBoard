import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'app/shared/shared.module';

import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SidebarComponent } from './sidebar/sidebar.component';
import { FlexLayoutModule } from '@angular/flex-layout';
@NgModule({
  imports: [SharedModule, CommonModule, RouterModule, NgbModule, FlexLayoutModule],
  declarations: [SidebarComponent],
  exports: [SidebarComponent],
  entryComponents: [SidebarComponent],
})
export class ComponentsModule {}
