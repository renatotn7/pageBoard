import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { PerguntaService } from '../service/pergunta.service';

import { PerguntaComponent } from './pergunta.component';

describe('Pergunta Management Component', () => {
  let comp: PerguntaComponent;
  let fixture: ComponentFixture<PerguntaComponent>;
  let service: PerguntaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [PerguntaComponent],
    })
      .overrideTemplate(PerguntaComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PerguntaComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PerguntaService);

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
    expect(comp.perguntas?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
