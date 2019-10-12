import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { ICFAOption } from 'app/shared/model/cfa-option.model';
import { createRequestOption } from 'app/shared/util/request-util';
import { Observable } from 'rxjs';

type EntityResponseType = HttpResponse<ICFAOption>;
type EntityArrayResponseType = HttpResponse<ICFAOption[]>;

@Injectable({ providedIn: 'root' })
export class CFAOptionService {
  public resourceUrl = SERVER_API_URL + 'api/cfa-options';

  constructor(protected http: HttpClient) {}

  update(cFAOption: ICFAOption): Observable<EntityResponseType> {
    return this.http.put<ICFAOption>(this.resourceUrl, cFAOption, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ICFAOption>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICFAOption[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  getAll(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICFAOption[]>(SERVER_API_URL + 'api/all-cfa-options', { params: options, observe: 'response' });
  }
}
