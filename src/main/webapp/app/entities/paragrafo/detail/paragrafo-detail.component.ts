import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IParagrafo } from '../paragrafo.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-paragrafo-detail',
  templateUrl: './paragrafo-detail.component.html',
})
export class ParagrafoDetailComponent implements OnInit {
  paragrafo: IParagrafo | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paragrafo }) => {
      this.paragrafo = paragrafo;
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
