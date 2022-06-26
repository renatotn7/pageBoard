import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAssunto } from '../assunto.model';

@Component({
  selector: 'jhi-assunto-detail',
  templateUrl: './assunto-detail.component.html',
})
export class AssuntoDetailComponent implements OnInit {
  assunto: IAssunto | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ assunto }) => {
      this.assunto = assunto;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
