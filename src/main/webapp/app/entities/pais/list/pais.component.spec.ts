import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { PaisService } from '../service/pais.service';

import { PaisComponent } from './pais.component';

describe('Pais Management Component', () => {
  let comp: PaisComponent;
  let fixture: ComponentFixture<PaisComponent>;
  let service: PaisService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [PaisComponent],
    })
      .overrideTemplate(PaisComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PaisComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PaisService);

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
    expect(comp.pais?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
