import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProjeto } from '../projeto.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-projeto-detail',
  templateUrl: './projeto-detail.component.html',
})
export class ProjetoDetailComponent implements OnInit {
  projeto: IProjeto | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projeto }) => {
      this.projeto = projeto;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
