import { TestBed } from '@angular/core/testing';

import { IslandService } from './island.service';

describe('IslandService', () => {
  let service: IslandService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IslandService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
