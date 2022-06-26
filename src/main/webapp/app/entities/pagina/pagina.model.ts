import { IParagrafo } from 'app/entities/paragrafo/paragrafo.model';
import { ILivro } from 'app/entities/livro/livro.model';

export interface IPagina {
  id?: number;
  numero?: number | null;
  texto?: string | null;
  planoDeAula?: string | null;
  imagemContentType?: string | null;
  imagem?: string | null;
  paragrafos?: IParagrafo[] | null;
  livro?: ILivro | null;
}

export class Pagina implements IPagina {
  constructor(
    public id?: number,
    public numero?: number | null,
    public texto?: string | null,
    public planoDeAula?: string | null,
    public imagemContentType?: string | null,
    public imagem?: string | null,
    public paragrafos?: IParagrafo[] | null,
    public livro?: ILivro | null
  ) {}
}

export function getPaginaIdentifier(pagina: IPagina): number | undefined {
  return pagina.id;
}
