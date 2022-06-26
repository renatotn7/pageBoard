import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { PageBoardService } from '../service/page-board.service';

import { PageBoardComponent } from './page-board.component';

describe('PageBoard Management Component', () => {
  let comp: PageBoardComponent;
  let fixture: ComponentFixture<PageBoardComponent>;
  let service: PageBoardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [PageBoardComponent],
    })
      .overrideTemplate(PageBoardComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PageBoardComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PageBoardService);

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
    expect(comp.pageBoards?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
