import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ProjetoService } from '../service/projeto.service';

import { ProjetoComponent } from './projeto.component';

describe('Projeto Management Component', () => {
  let comp: ProjetoComponent;
  let fixture: ComponentFixture<ProjetoComponent>;
  let service: ProjetoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ProjetoComponent],
    })
      .overrideTemplate(ProjetoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProjetoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProjetoService);

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
    expect(comp.projetos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
