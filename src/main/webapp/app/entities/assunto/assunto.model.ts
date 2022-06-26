import { ILivro } from 'app/entities/livro/livro.model';

export interface IAssunto {
  id?: number;
  nome?: string | null;
  livros?: ILivro[] | null;
}

export class Assunto implements IAssunto {
  constructor(public id?: number, public nome?: string | null, public livros?: ILivro[] | null) {}
}

export function getAssuntoIdentifier(assunto: IAssunto): number | undefined {
  return assunto.id;
}
