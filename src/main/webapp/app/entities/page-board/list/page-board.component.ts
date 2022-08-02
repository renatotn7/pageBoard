import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPageBoard } from '../page-board.model';
import { PageBoardService } from '../service/page-board.service';
import { PageBoardDeleteDialogComponent } from '../delete/page-board-delete-dialog.component';
import { IPagina } from '../../pagina/pagina.model';
import { PaginaService } from '../../pagina/service/pagina.service';
import { IParagrafo } from '../../paragrafo/paragrafo.model';
import { ParagrafoService } from '../../paragrafo/service/paragrafo.service';
import { PerguntaService } from '../../pergunta/service/pergunta.service';
import { IPergunta } from '../../pergunta/pergunta.model';
@Component({
  selector: 'jhi-page-board',
  templateUrl: './page-board.component.html',
  styleUrls: ['./page-board.component.scss']
})
export class PageBoardComponent implements OnInit {
  pageBoards?: IPageBoard[];
  pagina: IPagina | null = null;
  paragrafos?: IParagrafo[] | null=null ;
  paragrafo?: IParagrafo | null=null ;
  perguntas?: IPergunta[] | null=null;
  isLoading = false;
  showRespota=false;
  oldParagrafoChoosed=-1;
  paragrafosCss:string[]; 

  isGeneratedVisible=true;
  @ViewChild('cardsVisibility')
  cardsVisibility!: ElementRef;
  constructor(protected pageBoardService: PageBoardService,protected servicoPergunta:PerguntaService,protected servicoParagrafo:ParagrafoService,protected servicoPagina:PaginaService, protected modalService: NgbModal) {
this.paragrafosCss=[];
  }

  loadAll(): void {
    this.isLoading = true;

  }

  /*onClickGerarPerguntas(idParagrafo):void{

    servicoPagina.gerarPerguntas(idParagrafo);

  }*/
  ngOnInit(): void {
   
   // this.loadAll();
   this.isLoading=true;
   
   this.servicoPagina.find(16).subscribe(( pagina ) => {
     this.pagina=pagina.body;
    // this.paragrafos=pagina.body?.paragrafos;
    
     
    });
    this.servicoParagrafo.findByPagina(11).subscribe(( paragrafos ) => {
    //  this.pagina=pagina.body;
      this.paragrafos=paragrafos.body;
      this.paragrafos?.forEach((paragrafo)=> {
        this.paragrafosCss.push("whiteparagrafo");
      });
    });
   // findByPagina
   this.isLoading=false;
  }
 
  loadperguntas(paragrafo:IParagrafo,index:number):void{
    this.paragrafo=paragrafo;
   
    this.servicoPergunta.findByParagrafo(paragrafo.id!).subscribe(( perguntas ) => {
      //  this.pagina=pagina.body;
        this.perguntas=perguntas.body;
       
       
       });
       this.oldParagrafoChoosed
       this.paragrafosCss[index]="blueparagrafo"
       this.paragrafosCss[ this.oldParagrafoChoosed]="whiteparagrafo"
       this.oldParagrafoChoosed=index;
  }
  generateQuestion():void{
    let i=1;
    i=i+1;
  }
  trackId(_index: number, item: IPageBoard): number {
    return item.id!;
  }

  delete(pageBoard: IPageBoard): void {
    const modalRef = this.modalService.open(PageBoardDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pageBoard = pageBoard;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
  public showResp():void{
    this.showRespota=true;
  }
  public hideResp():void{
    this.showRespota=false;
  }
  
}
