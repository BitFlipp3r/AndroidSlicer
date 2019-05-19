import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { IAndroidVersion } from 'app/shared/model/android-version.model';
import { IAndroidClass } from 'app/shared/model/android-class.model';

@Injectable({ providedIn: 'root' })
export class AndroidOptionsService {
  public resourceUrl = SERVER_API_URL + 'api/android-options';

  constructor(private http: HttpClient) {}

  getAndroidVersions(): Observable<HttpResponse<IAndroidVersion[]>> {
    return this.http.get<IAndroidVersion[]>(`${this.resourceUrl}/android-versions`, { observe: 'response' });
  }

  getAndroidClasses(path: string): Observable<HttpResponse<IAndroidClass[]>> {
    return this.http.get<IAndroidClass[]>(`${this.resourceUrl}/system-services`, {
      params: new HttpParams().set('path', path),
      observe: 'response'
    });
  }

  getServiceSource(path: string): Observable<any> {
    return this.http.get(`${this.resourceUrl}/source-file`, {
      responseType: 'text',
      params: new HttpParams().set('path', path),
      observe: 'response'
    });
  }

  getSeedStatements(): Observable<HttpResponse<string[]>> {
    return this.http.get<string[]>(`${this.resourceUrl}/seed-statements`, { observe: 'response' });
  }
}
