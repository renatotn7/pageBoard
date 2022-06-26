import { ICidade } from 'app/entities/cidade/cidade.model';
import { IEstado } from 'app/entities/estado/estado.model';
import { IPais } from 'app/entities/pais/pais.model';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';

export interface IEndereco {
  id?: number;
  logradouro?: string | null;
  numero?: number | null;
  complemento?: string | null;
  bairro?: string | null;
  cEP?: string | null;
  cidade?: ICidade | null;
  estado?: IEstado | null;
  pais?: IPais | null;
  pessoa?: IPessoa | null;
}

export class Endereco implements IEndereco {
  constructor(
    public id?: number,
    public logradouro?: string | null,
    public numero?: number | null,
    public complemento?: string | null,
    public bairro?: string | null,
    public cEP?: string | null,
    public cidade?: ICidade | null,
    public estado?: IEstado | null,
    public pais?: IPais | null,
    public pessoa?: IPessoa | null
  ) {}
}

export function getEnderecoIdentifier(endereco: IEndereco): number | undefined {
  return endereco.id;
}
