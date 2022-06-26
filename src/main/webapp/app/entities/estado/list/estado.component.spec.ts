import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { EstadoService } from '../service/estado.service';

import { EstadoComponent } from './estado.component';

describe('Estado Management Component', () => {
  let comp: EstadoComponent;
  let fixture: ComponentFixture<EstadoComponent>;
  let service: EstadoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [EstadoComponent],
    })
      .overrideTemplate(EstadoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EstadoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(EstadoService);

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
    expect(comp.estados?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
