import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EstadoDetailComponent } from './estado-detail.component';

describe('Estado Management Detail Component', () => {
  let comp: EstadoDetailComponent;
  let fixture: ComponentFixture<EstadoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EstadoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ estado: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EstadoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EstadoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load estado on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.estado).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
