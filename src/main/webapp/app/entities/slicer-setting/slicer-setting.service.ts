import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ISlicerSetting } from 'app/shared/model/slicer-setting.model';

type EntityResponseType = HttpResponse<ISlicerSetting>;
type EntityArrayResponseType = HttpResponse<ISlicerSetting[]>;

@Injectable({ providedIn: 'root' })
export class SlicerSettingService {
  public resourceUrl = SERVER_API_URL + 'api/slicer-settings';

  constructor(protected http: HttpClient) {}

  update(slicerSetting: ISlicerSetting): Observable<EntityResponseType> {
    return this.http.put<ISlicerSetting>(this.resourceUrl, slicerSetting, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ISlicerSetting>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISlicerSetting[]>(this.resourceUrl, { params: options, observe: 'response' });
  }
}
