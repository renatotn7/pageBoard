import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'anexo-de-paragrafo',
        data: { pageTitle: 'pageBoardApp.anexoDeParagrafo.home.title' },
        loadChildren: () => import('./anexo-de-paragrafo/anexo-de-paragrafo.module').then(m => m.AnexoDeParagrafoModule),
      },
      {
        path: 'assunto',
        data: { pageTitle: 'pageBoardApp.assunto.home.title' },
        loadChildren: () => import('./assunto/assunto.module').then(m => m.AssuntoModule),
      },
      {
        path: 'cidade',
        data: { pageTitle: 'pageBoardApp.cidade.home.title' },
        loadChildren: () => import('./cidade/cidade.module').then(m => m.CidadeModule),
      },
      {
        path: 'endereco',
        data: { pageTitle: 'pageBoardApp.endereco.home.title' },
        loadChildren: () => import('./endereco/endereco.module').then(m => m.EnderecoModule),
      },
      {
        path: 'estado',
        data: { pageTitle: 'pageBoardApp.estado.home.title' },
        loadChildren: () => import('./estado/estado.module').then(m => m.EstadoModule),
      },
      {
        path: 'livro',
        data: { pageTitle: 'pageBoardApp.livro.home.title' },
        loadChildren: () => import('./livro/livro.module').then(m => m.LivroModule),
      },
      {
        path: 'page-board',
        data: { pageTitle: 'pageBoardApp.pageBoard.home.title' },
        loadChildren: () => import('./page-board/page-board.module').then(m => m.PageBoardModule),
      },
      {
        path: 'pagina',
        data: { pageTitle: 'pageBoardApp.pagina.home.title' },
        loadChildren: () => import('./pagina/pagina.module').then(m => m.PaginaModule),
      },
      {
        path: 'pais',
        data: { pageTitle: 'pageBoardApp.pais.home.title' },
        loadChildren: () => import('./pais/pais.module').then(m => m.PaisModule),
      },
      {
        path: 'paragrafo',
        data: { pageTitle: 'pageBoardApp.paragrafo.home.title' },
        loadChildren: () => import('./paragrafo/paragrafo.module').then(m => m.ParagrafoModule),
      },
      {
        path: 'pergunta',
        data: { pageTitle: 'pageBoardApp.pergunta.home.title' },
        loadChildren: () => import('./pergunta/pergunta.module').then(m => m.PerguntaModule),
      },
      {
        path: 'pessoa',
        data: { pageTitle: 'pageBoardApp.pessoa.home.title' },
        loadChildren: () => import('./pessoa/pessoa.module').then(m => m.PessoaModule),
      },
      {
        path: 'projeto',
        data: { pageTitle: 'pageBoardApp.projeto.home.title' },
        loadChildren: () => import('./projeto/projeto.module').then(m => m.ProjetoModule),
      },
      {
        path: 'resposta',
        data: { pageTitle: 'pageBoardApp.resposta.home.title' },
        loadChildren: () => import('./resposta/resposta.module').then(m => m.RespostaModule),
      },
      {
        path: 'usuario',
        data: { pageTitle: 'pageBoardApp.usuario.home.title' },
        loadChildren: () => import('./usuario/usuario.module').then(m => m.UsuarioModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
