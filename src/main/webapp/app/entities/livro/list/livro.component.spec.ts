import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { LivroService } from '../service/livro.service';

import { LivroComponent } from './livro.component';

describe('Livro Management Component', () => {
  let comp: LivroComponent;
  let fixture: ComponentFixture<LivroComponent>;
  let service: LivroService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [LivroComponent],
    })
      .overrideTemplate(LivroComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LivroComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(LivroService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.livros?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
