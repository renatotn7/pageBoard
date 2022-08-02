import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { SidebarComponent } from '../components/sidebar/sidebar.component';
import { ComponentsModule } from '../components/components.module';
import { FlexLayoutModule } from '@angular/flex-layout';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([HOME_ROUTE]), ComponentsModule, FlexLayoutModule],
  declarations: [HomeComponent],
  exports: [SidebarComponent],
})
export class HomeModule {}
