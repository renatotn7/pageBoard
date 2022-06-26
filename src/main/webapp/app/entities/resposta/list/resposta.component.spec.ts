import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { RespostaService } from '../service/resposta.service';

import { RespostaComponent } from './resposta.component';

describe('Resposta Management Component', () => {
  let comp: RespostaComponent;
  let fixture: ComponentFixture<RespostaComponent>;
  let service: RespostaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [RespostaComponent],
    })
      .overrideTemplate(RespostaComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RespostaComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RespostaService);

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
    expect(comp.respostas?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
