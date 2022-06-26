import { IPergunta } from 'app/entities/pergunta/pergunta.model';
import { IPagina } from 'app/entities/pagina/pagina.model';

export interface IParagrafo {
  id?: number;
  numero?: number | null;
  texto?: string | null;
  resumo?: string | null;
  perguntas?: IPergunta[] | null;
  pagina?: IPagina | null;
}

export class Paragrafo implements IParagrafo {
  constructor(
    public id?: number,
    public numero?: number | null,
    public texto?: string | null,
    public resumo?: string | null,
    public perguntas?: IPergunta[] | null,
    public pagina?: IPagina | null
  ) {}
}

export function getParagrafoIdentifier(paragrafo: IParagrafo): number | undefined {
  return paragrafo.id;
}
