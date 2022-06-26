export interface IResposta {
  id?: number;
  texto?: string | null;
}

export class Resposta implements IResposta {
  constructor(public id?: number, public texto?: string | null) {}
}

export function getRespostaIdentifier(resposta: IResposta): number | undefined {
  return resposta.id;
}
