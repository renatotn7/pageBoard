import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IResposta } from '../resposta.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-resposta-detail',
  templateUrl: './resposta-detail.component.html',
})
export class RespostaDetailComponent implements OnInit {
  resposta: IResposta | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resposta }) => {
      this.resposta = resposta;
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
