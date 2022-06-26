import { IPais } from 'app/entities/pais/pais.model';

export interface IEstado {
  id?: number;
  nome?: string | null;
  uf?: string | null;
  pais?: IPais | null;
}

export class Estado implements IEstado {
  constructor(public id?: number, public nome?: string | null, public uf?: string | null, public pais?: IPais | null) {}
}

export function getEstadoIdentifier(estado: IEstado): number | undefined {
  return estado.id;
}
