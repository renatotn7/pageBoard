import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PageBoardDetailComponent } from './page-board-detail.component';

describe('PageBoard Management Detail Component', () => {
  let comp: PageBoardDetailComponent;
  let fixture: ComponentFixture<PageBoardDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PageBoardDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ pageBoard: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PageBoardDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PageBoardDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load pageBoard on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.pageBoard).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
