export interface IPageBoard {
  id?: number;
}

export class PageBoard implements IPageBoard {
  constructor(public id?: number) {}
}

export function getPageBoardIdentifier(pageBoard: IPageBoard): number | undefined {
  return pageBoard.id;
}
