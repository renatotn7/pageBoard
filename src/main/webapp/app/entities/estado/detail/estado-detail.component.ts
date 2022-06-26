import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEstado } from '../estado.model';

@Component({
  selector: 'jhi-estado-detail',
  templateUrl: './estado-detail.component.html',
})
export class EstadoDetailComponent implements OnInit {
  estado: IEstado | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ estado }) => {
      this.estado = estado;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
