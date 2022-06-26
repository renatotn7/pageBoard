import { IParagrafo } from 'app/entities/paragrafo/paragrafo.model';
import { TipoAnexoDeParagrafo } from 'app/entities/enumerations/tipo-anexo-de-paragrafo.model';

export interface IAnexoDeParagrafo {
  id?: number;
  tipo?: TipoAnexoDeParagrafo | null;
  value?: string | null;
  paragrafo?: IParagrafo | null;
}

export class AnexoDeParagrafo implements IAnexoDeParagrafo {
  constructor(
    public id?: number,
    public tipo?: TipoAnexoDeParagrafo | null,
    public value?: string | null,
    public paragrafo?: IParagrafo | null
  ) {}
}

export function getAnexoDeParagrafoIdentifier(anexoDeParagrafo: IAnexoDeParagrafo): number | undefined {
  return anexoDeParagrafo.id;
}
