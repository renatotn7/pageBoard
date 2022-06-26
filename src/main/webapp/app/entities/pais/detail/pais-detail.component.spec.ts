import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PaisDetailComponent } from './pais-detail.component';

describe('Pais Management Detail Component', () => {
  let comp: PaisDetailComponent;
  let fixture: ComponentFixture<PaisDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PaisDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ pais: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PaisDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PaisDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load pais on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.pais).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
