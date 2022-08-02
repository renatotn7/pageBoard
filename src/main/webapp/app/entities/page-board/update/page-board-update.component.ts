import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPageBoard } from '../page-board.model';
import { PageBoardService } from '../service/page-board.service';
import { PageBoardDeleteDialogComponent } from '../delete/page-board-delete-dialog.component';
import { IPagina } from '../../pagina/pagina.model';
import { PaginaService } from '../../pagina/service/pagina.service';
import { IParagrafo, Paragrafo } from '../../paragrafo/paragrafo.model';
import { ParagrafoService } from '../../paragrafo/service/paragrafo.service';
import { PerguntaService } from '../../pergunta/service/pergunta.service';
import { IPergunta } from '../../pergunta/pergunta.model';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { IAnexoDeParagrafo } from '../../anexo-de-paragrafo/anexo-de-paragrafo.model';
import { AnexoDeParagrafoService } from '../../anexo-de-paragrafo/service/anexo-de-paragrafo.service';
import { TipoAnexoDeParagrafo } from '../../enumerations/tipo-anexo-de-paragrafo.model';

@Component({
  selector: 'jhi-page-board',
  templateUrl: './page-board-update.component.html',
  styleUrls: ['./page-board-update.component.scss']
})
export class PageBoardUpdateComponent implements OnInit {
  pageBoards?: IPageBoard[];
  pagina: IPagina | null = null;
  paragrafos?: IParagrafo[] | null=null ;
  paragrafo?: IParagrafo | null=null ;
  perguntas?: IPergunta[] | null=null;
  anexosDeParagrafo?:IAnexoDeParagrafo[] | null=null;
  isLoading = false;
  showRespota=false;
  isPerguntasVisible=true;
  isAnexoFacilitadoVisible= false;
  isAnexoResumoVisible= false;
  isExpTitVisible= false;
  novoParagrafoChoosed=-1;
  oldParagrafoChoosed=-1;
  paragrafosCss:string[]; 
  perguntasPanelActiveCss="active";
  facilitadoPanelActiveCss="";
  resumoPanelActiveCss="";
  ExpTitPanelActiveCss="";

  isGeneratedVisible=true;
  @ViewChild('cardsVisibility')
  cardsVisibility!: ElementRef;
  @ViewChild('panelPerguntas')
  panelPerguntas!: ElementRef;
  @ViewChild('panelFacilitado')
  panelFacilitado!: ElementRef;
  @ViewChild('panelResumo')
  panelResumo!: ElementRef;
  @ViewChild('panelResumo')
  panelExpTit!: ElementRef;
  constructor(protected pageBoardService: PageBoardService,
              protected servicoPergunta:PerguntaService,
              protected servicoParagrafo:ParagrafoService,
              protected servicoAnexoDeParagrafo:AnexoDeParagrafoService,
              protected servicoPagina:PaginaService, 
              protected modalService: NgbModal,
              protected activatedRoute: ActivatedRoute) {
              this.paragrafosCss=[];
  }

  /**
   * 
   */
  loadAll(): void {
    this.isLoading = true;

  }

/**
 * Called when the user clicks on the button to add a new pageBoard.
 */

  
  ngOnInit(): void {
    this.isLoading=true;
   
    this.activatedRoute.paramMap.subscribe(
      (params: ParamMap) => {
      
        let numero =0;
        numero=parseInt(params.get('id')?? '0',10) ;
        this.servicoPagina.find(numero).subscribe(( page )=>{
          
          this.pagina=page.body;
         
          this.servicoParagrafo.findByPagina(this.pagina!.id!).subscribe(( paragrafos ) => {
           
              this.paragrafos=paragrafos.body;

              if(this.paragrafos?.length===0){

             
                this.servicoPagina.createBlocos(this.pagina!).subscribe(( paragrafos1) => {
                  alert("oi");
                    this.paragrafos=paragrafos1.body;
                 
                    this.paragrafosCss=[];
                    this.paragrafos?.forEach((paragrafo)=> {
                    
                      this.paragrafosCss.push("whiteparagrafo");
                    });
                  
                    this.isLoading=false;
                  //  this.isGeneratedVisible=false;
                  });

                }else{
                 
                  this.paragrafos?.forEach((paragrafo)=> {

                    this.paragrafosCss.push("whiteparagrafo");
                  });
                }
               /* const paragrafo: IParagrafo=   new Paragrafo(
                  -1,
                  0,
                  this.pagina!.texto,
                  null,
                  null,
                  this.pagina
                ) ;
                this.paragrafos.push(paragrafo)

                let i=1;
                i=2;*/
                
              
            
            
            });
           // findByPagina
           this.isLoading=false;
        });
      });
  
    
    
    
    
   // this.loadAll();
   
   
  /* this.servicoPagina.find(16).subscribe(( pagina ) => {
     this.pagina=pagina.body;
    // this.paragrafos=pagina.body?.paragrafos;
    
     
    });*/
  
  }
  
  loadperguntas(paragrafo:IParagrafo,index:number):void{
  
    this.paragrafo=paragrafo;
    if(this.isPerguntasVisible){
    this.servicoPergunta.findByParagrafo(paragrafo.id!).subscribe(( perguntas ) => {
   
      //  this.pagina=pagina.body;
        this.perguntas=perguntas.body;
       
       
       });
      }
      if(this.isAnexoFacilitadoVisible){
      
      this.servicoAnexoDeParagrafo.findByParagrafo(paragrafo.id!,TipoAnexoDeParagrafo.TXTSIMPLIFICADO).subscribe(( anexosDeParagrafo ) => {
      
        this.anexosDeParagrafo=anexosDeParagrafo.body;
       
       
       
       });      
      }
      if(this.isAnexoResumoVisible){
        
        this.servicoAnexoDeParagrafo.findByParagrafo(paragrafo.id!,TipoAnexoDeParagrafo.TXTTOPICOS).subscribe(( anexosDeParagrafo ) => {
        
          this.anexosDeParagrafo=anexosDeParagrafo.body;
         
         
         
         });      
        }
        if(this.isExpTitVisible){
         
          this.servicoAnexoDeParagrafo.findByParagrafo(paragrafo.id!,TipoAnexoDeParagrafo.EXPLICATEXTOCTIT).subscribe(( anexosDeParagrafo ) => {
          
            this.anexosDeParagrafo=anexosDeParagrafo.body;
           
           
           
           });      
          }
  
       this.novoParagrafoChoosed=index;
       this.oldParagrafoChoosed
       this.paragrafosCss[index]="blueparagrafo"
       this.paragrafosCss[ this.oldParagrafoChoosed]="whiteparagrafo"
       this.oldParagrafoChoosed=index;
  }
 
  generateQuestion():void{
    if(this.isPerguntasVisible){
      this.servicoPagina.generatePerguntas(this.paragrafos![this.novoParagrafoChoosed]! ).subscribe(( paragrafo )=>{
        this.perguntas=paragrafo.body!.perguntas;
        this.paragrafosCss[this.novoParagrafoChoosed]="blueparagrafo"

        //  this.pagina=page.body;
        
      });
  } else if(this.isAnexoFacilitadoVisible){
    this.servicoPagina.generateTextoFacilitado(this.paragrafos![this.novoParagrafoChoosed]! ).subscribe(( paragrafo )=>{
      this.anexosDeParagrafo=paragrafo.body!.anexos;
      this.paragrafosCss[this.novoParagrafoChoosed]="blueparagrafo"
    /*  if(this.anexosDeParagrafo!.length>0){
        for(  let i=0;i<this.anexosDeParagrafo!.length;i++){
         
        this.servicoAnexoDePa
        ragrafo.create(this.anexosDeParagrafo![i]);
      }

      //  this.pagina=page.body;
    }*/
    });
  } else if(this.isAnexoResumoVisible){
    
    this.servicoPagina.generateEmTopicos(this.paragrafos![this.novoParagrafoChoosed]! ).subscribe(( paragrafo )=>{
      this.anexosDeParagrafo=paragrafo.body!.anexos;
      this.paragrafosCss[this.novoParagrafoChoosed]="blueparagrafo"
    /*  if(this.anexosDeParagrafo!.length>0){
        for(  let i=0;i<this.anexosDeParagrafo!.length;i++){
         
        this.servicoAnexoDeParagrafo.create(this.anexosDeParagrafo![i]);
      }

      //  this.pagina=page.body;
    }*/
    });
  }else if(this.isExpTitVisible){
    
    this.servicoPagina.generateEmTitulos(this.paragrafos![this.novoParagrafoChoosed]! ).subscribe(( paragrafo )=>{
      this.anexosDeParagrafo=paragrafo.body!.anexos;
      this.paragrafosCss[this.novoParagrafoChoosed]="blueparagrafo"
    /*  if(this.anexosDeParagrafo!.length>0){
        for(  let i=0;i<this.anexosDeParagrafo!.length;i++){
         
        this.servicoAnexoDeParagrafo.create(this.anexosDeParagrafo![i]);
      }

      //  this.pagina=page.body;
    }*/
    });
  }
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

  public showPerguntasPanel():void{
    this.loadperguntas( this.paragrafos![this.novoParagrafoChoosed]!,this.novoParagrafoChoosed);
    this.ExpTitPanelActiveCss="";  
    this.perguntasPanelActiveCss="active";
    this.facilitadoPanelActiveCss="";
    this.resumoPanelActiveCss="";
    this.isExpTitVisible=false;
    this.isPerguntasVisible=true;
    this.isAnexoFacilitadoVisible=false;
    this.isAnexoResumoVisible=false;
    this.panelPerguntas.nativeElement.style.display="block";
    this.panelFacilitado.nativeElement.style.display="none";
    this.panelResumo.nativeElement.style.display="none";
    this.panelExpTit.nativeElement.style.display="none";
  }
  public showFacilitadoPanel():void{
    this.loadperguntas( this.paragrafos![this.novoParagrafoChoosed]!,this.novoParagrafoChoosed);
    this.ExpTitPanelActiveCss="";  
    this.perguntasPanelActiveCss="";
    this.facilitadoPanelActiveCss="active";
    this.resumoPanelActiveCss="";
    this.isExpTitVisible=false;
    this.isPerguntasVisible=false;
    this.isAnexoFacilitadoVisible=true;
    this.isAnexoResumoVisible=false;
    this.panelPerguntas.nativeElement.style.display="none";
    this.panelFacilitado.nativeElement.style.display="block";
    this.panelResumo.nativeElement.style.display="none";
    this.panelExpTit.nativeElement.style.display="none";
  }
  public error():void{
    alert('Anexo salvo com erro');
  }
  public ok():void{
    alert('Anexo salvo com sucesso');
  }
  public saveAnexo():void{
 
    alert(this.anexosDeParagrafo![0]!.id);
   
            
            this.servicoAnexoDeParagrafo.create(this.anexosDeParagrafo![0]).subscribe({
              next: this.ok,
              error:this.error,
            });
          
  }

  public showResumoPanel():void{
    this.loadperguntas( this.paragrafos![this.novoParagrafoChoosed]!,this.novoParagrafoChoosed);
    this.ExpTitPanelActiveCss="";  
    this.perguntasPanelActiveCss="";
    this.facilitadoPanelActiveCss="";
    this.resumoPanelActiveCss="active";
    this.isExpTitVisible=false;
    this.isPerguntasVisible=false;
    this.isAnexoFacilitadoVisible=false;
    this.isAnexoResumoVisible=true;
    this.panelPerguntas.nativeElement.style.display="none";
    this.panelFacilitado.nativeElement.style.display="none";
    this.panelResumo.nativeElement.style.display="block";
    this.panelExpTit.nativeElement.style.display="none";
  }
  public showExpTitPanel():void{
    this.loadperguntas( this.paragrafos![this.novoParagrafoChoosed]!,this.novoParagrafoChoosed);
    this.ExpTitPanelActiveCss="active";  
    this.perguntasPanelActiveCss="";
    this.facilitadoPanelActiveCss="";
    this.resumoPanelActiveCss="";
    this.isExpTitVisible=true;
    this.isPerguntasVisible=false;
    this.isAnexoFacilitadoVisible=false;
    this.isAnexoResumoVisible=false;
    this.panelPerguntas.nativeElement.style.display="none";
    this.panelFacilitado.nativeElement.style.display="none";
    this.panelResumo.nativeElement.style.display="none";
    this.panelExpTit.nativeElement.style.display="block";
  }  
  public showResp():void{
    this.showRespota=true;
  }
  public hideResp():void{
    this.showRespota=false;
  }
  
}

