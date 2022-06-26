import { IPagina } from 'app/entities/pagina/pagina.model';
import { IProjeto } from 'app/entities/projeto/projeto.model';
import { IAssunto } from 'app/entities/assunto/assunto.model';

export interface ILivro {
  id?: number;
  nomeLivro?: string | null;
  editora?: string | null;
  autor?: string | null;
  anoDePublicacao?: number | null;
  tags?: string | null;
  paginas?: IPagina[] | null;
  projetos?: IProjeto[] | null;
  assunto?: IAssunto | null;
}

export class Livro implements ILivro {
  constructor(
    public id?: number,
    public nomeLivro?: string | null,
    public editora?: string | null,
    public autor?: string | null,
    public anoDePublicacao?: number | null,
    public tags?: string | null,
    public paginas?: IPagina[] | null,
    public projetos?: IProjeto[] | null,
    public assunto?: IAssunto | null
  ) {}
}

export function getLivroIdentifier(livro: ILivro): number | undefined {
  return livro.id;
}
