import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPageBoard } from '../page-board.model';

@Component({
  selector: 'jhi-page-board-detail',
  templateUrl: './page-board-detail.component.html',
})
export class PageBoardDetailComponent implements OnInit {
  pageBoard: IPageBoard | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pageBoard }) => {
      this.pageBoard = pageBoard;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
