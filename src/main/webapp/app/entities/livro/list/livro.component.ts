import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILivro } from '../livro.model';
import { LivroService } from '../service/livro.service';
import { LivroDeleteDialogComponent } from '../delete/livro-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';
import { Chips } from 'primeng/chips';

@Component({
  selector: 'jhi-livro',
  templateUrl: './livro.component.html',
  styleUrls: ['./livro.component.scss'],
 
})
export class LivroComponent implements OnInit {
  livros?: ILivro[];
  textForSearch: any;
  isLoading = false;
  tagsCss:string[]; 
  isTableVisible=false;
  isCardsVisible=true;
  isBuscaTextoVisible=true;
  isBuscaTagsVisible=false;
  @ViewChild('cardsVisibility')
  cardsVisibility!: ElementRef;
  @ViewChild('tableVisibility')
  tableVisibility!: ElementRef;
  tags1=""
  @ViewChild('buscaTextoComponent')
  buscaTextoComponent!: ElementRef;
  @ViewChild('buscaTagsComponent')
  buscaTagsComponent!: ElementRef;
  selectedValue: string;
  @ViewChild('chipcomponent', {static: false}) chipcomponent!: ElementRef;
    
  constructor(protected livroService: LivroService, protected dataUtils: DataUtils, protected modalService: NgbModal) {
    this.tagsCss=['bluetag','yelowtag','greentag','orangetag']
    this.selectedValue="or";
  }
  toggleListVisible():void{
    this.isTableVisible= !this.isTableVisible;
    this.isCardsVisible=!this.isCardsVisible;
  }
  toggleBuscaVisible():void{
    
    this.isBuscaTextoVisible= !this.isBuscaTextoVisible;
    this.isBuscaTagsVisible=!this.isBuscaTagsVisible;
    if(this.isBuscaTagsVisible){
      
      this.chipcomponent.nativeElement.click();
    }
  }
  findByText():void{
    this.livroService.queryByText(this.textForSearch).subscribe({
      next: (res: HttpResponse<ILivro[]>) => {
      //  alert(1);
        this.isLoading = false;
        this.livros = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }
  
  findByTagsAnd():void{
    if(this.selectedValue==="and"){
    this.livroService.queryByAndTags(this.tags1).subscribe({
      next: (res: HttpResponse<ILivro[]>) => {
      //  alert(1);
        this.isLoading = false;
        this.livros = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }
  if(this.selectedValue==="or"){
    this.livroService.queryByOrTags(this.tags1).subscribe({
      next: (res: HttpResponse<ILivro[]>) => {
      //  alert(1);
        this.isLoading = false;
        this.livros = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }
    
  }
 
  loadAll(): void {
    this.isLoading = true;

    this.livroService.query().subscribe({
      next: (res: HttpResponse<ILivro[]>) => {
        this.isLoading = false;
        this.livros = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ILivro): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(livro: ILivro): void {
    const modalRef = this.modalService.open(LivroDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.livro = livro;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
  splitTag(tag: string):string[] {
    return tag.split(';');
}

}
