export interface IAndroidVersion {
  version?: number;
  path?: string;
}

export class AndroidVersion implements IAndroidVersion {
  constructor(public version?: number, public path?: string) {}
}
