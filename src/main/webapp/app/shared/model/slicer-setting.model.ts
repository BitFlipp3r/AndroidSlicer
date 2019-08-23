export interface ISlicerSetting {
  id?: string;
  key?: string;
  value?: string;
  description?: string;
}

export class SlicerSetting implements ISlicerSetting {
  constructor(public id?: string, public key?: string, public value?: string, public description?: string) {}
}
