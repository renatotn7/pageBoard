export interface IPais {
  id?: number;
  nome?: string | null;
  sigla?: string | null;
}

export class Pais implements IPais {
  constructor(public id?: number, public nome?: string | null, public sigla?: string | null) {}
}

export function getPaisIdentifier(pais: IPais): number | undefined {
  return pais.id;
}
