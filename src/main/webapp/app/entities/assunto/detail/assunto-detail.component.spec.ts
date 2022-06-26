import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AssuntoDetailComponent } from './assunto-detail.component';

describe('Assunto Management Detail Component', () => {
  let comp: AssuntoDetailComponent;
  let fixture: ComponentFixture<AssuntoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AssuntoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ assunto: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AssuntoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AssuntoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load assunto on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.assunto).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
