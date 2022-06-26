import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { IProjeto } from 'app/entities/projeto/projeto.model';

export interface IUsuario {
  id?: number;
  login?: string | null;
  email?: string | null;
  fotoContentType?: string | null;
  foto?: string | null;
  pessoa?: IPessoa | null;
  projetos?: IProjeto[] | null;
}

export class Usuario implements IUsuario {
  constructor(
    public id?: number,
    public login?: string | null,
    public email?: string | null,
    public fotoContentType?: string | null,
    public foto?: string | null,
    public pessoa?: IPessoa | null,
    public projetos?: IProjeto[] | null
  ) {}
}

export function getUsuarioIdentifier(usuario: IUsuario): number | undefined {
  return usuario.id;
}
