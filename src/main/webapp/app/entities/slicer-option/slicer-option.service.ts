import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ISlicerOption } from 'app/shared/model/slicer-option.model';

type EntityResponseType = HttpResponse<ISlicerOption>;
type EntityArrayResponseType = HttpResponse<ISlicerOption[]>;

@Injectable({ providedIn: 'root' })
export class SlicerOptionService {
  public resourceUrl = SERVER_API_URL + 'api/slicer-options';

  constructor(protected http: HttpClient) {}

  create(slicerOption: ISlicerOption): Observable<EntityResponseType> {
    return this.http.post<ISlicerOption>(this.resourceUrl, slicerOption, { observe: 'response' });
  }

  update(slicerOption: ISlicerOption): Observable<EntityResponseType> {
    return this.http.put<ISlicerOption>(this.resourceUrl, slicerOption, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ISlicerOption>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISlicerOption[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
