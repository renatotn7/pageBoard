import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ParagrafoService } from '../service/paragrafo.service';

import { ParagrafoComponent } from './paragrafo.component';

describe('Paragrafo Management Component', () => {
  let comp: ParagrafoComponent;
  let fixture: ComponentFixture<ParagrafoComponent>;
  let service: ParagrafoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ParagrafoComponent],
    })
      .overrideTemplate(ParagrafoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ParagrafoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ParagrafoService);

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
    expect(comp.paragrafos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
