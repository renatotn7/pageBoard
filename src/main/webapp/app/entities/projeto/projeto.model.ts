import { IUsuario } from 'app/entities/usuario/usuario.model';
import { ILivro } from 'app/entities/livro/livro.model';

export interface IProjeto {
  id?: number;
  nome?: string | null;
  descricao?: string | null;
  imagemContentType?: string | null;
  imagem?: string | null;
  tags?: string | null;
  usuario?: IUsuario | null;
  livros?: ILivro[] | null;
}

export class Projeto implements IProjeto {
  constructor(
    public id?: number,
    public nome?: string | null,
    public descricao?: string | null,
    public imagemContentType?: string | null,
    public imagem?: string | null,
    public tags?: string | null,
    public usuario?: IUsuario | null,
    public livros?: ILivro[] | null
  ) {}
}

export function getProjetoIdentifier(projeto: IProjeto): number | undefined {
  return projeto.id;
}
