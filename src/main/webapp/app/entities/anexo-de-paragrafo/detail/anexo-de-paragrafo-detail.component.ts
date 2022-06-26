import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAnexoDeParagrafo } from '../anexo-de-paragrafo.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-anexo-de-paragrafo-detail',
  templateUrl: './anexo-de-paragrafo-detail.component.html',
})
export class AnexoDeParagrafoDetailComponent implements OnInit {
  anexoDeParagrafo: IAnexoDeParagrafo | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ anexoDeParagrafo }) => {
      this.anexoDeParagrafo = anexoDeParagrafo;
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
