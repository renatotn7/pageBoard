import { IParagrafo } from 'app/entities/paragrafo/paragrafo.model';

export interface IPergunta {
  id?: number;
  questao?: string | null;
  resposta?: string | null;
  paragrafo?: IParagrafo | null;
}

export class Pergunta implements IPergunta {
  constructor(public id?: number, public questao?: string | null, public resposta?: string | null, public paragrafo?: IParagrafo | null) {}
}

export function getPerguntaIdentifier(pergunta: IPergunta): number | undefined {
  return pergunta.id;
}
