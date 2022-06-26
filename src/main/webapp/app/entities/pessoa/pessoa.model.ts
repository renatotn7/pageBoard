import dayjs from 'dayjs/esm';
import { IEndereco } from 'app/entities/endereco/endereco.model';

export interface IPessoa {
  id?: number;
  nome?: string | null;
  dataDeNascimento?: dayjs.Dayjs | null;
  enderecos?: IEndereco[] | null;
}

export class Pessoa implements IPessoa {
  constructor(
    public id?: number,
    public nome?: string | null,
    public dataDeNascimento?: dayjs.Dayjs | null,
    public enderecos?: IEndereco[] | null
  ) {}
}

export function getPessoaIdentifier(pessoa: IPessoa): number | undefined {
  return pessoa.id;
}
