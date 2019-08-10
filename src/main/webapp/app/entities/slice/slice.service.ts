import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ISlice } from 'app/shared/model/slice.model';

type EntityResponseType = HttpResponse<ISlice>;
type EntityArrayResponseType = HttpResponse<ISlice[]>;

@Injectable({ providedIn: 'root' })
export class SliceService {
  public resourceUrl = SERVER_API_URL + 'api/slices';

  constructor(protected http: HttpClient) {}

  create(slice: ISlice): Observable<EntityResponseType> {
    return this.http.post<ISlice>(this.resourceUrl, slice, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ISlice>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISlice[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
