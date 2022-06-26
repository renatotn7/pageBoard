import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILivro } from '../livro.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-livro-detail',
  templateUrl: './livro-detail.component.html',
})
export class LivroDetailComponent implements OnInit {
  livro: ILivro | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ livro }) => {
      this.livro = livro;
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
