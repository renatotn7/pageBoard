import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPergunta } from '../pergunta.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-pergunta-detail',
  templateUrl: './pergunta-detail.component.html',
})
export class PerguntaDetailComponent implements OnInit {
  pergunta: IPergunta | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pergunta }) => {
      this.pergunta = pergunta;
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
