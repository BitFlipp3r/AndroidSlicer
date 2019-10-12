import { CFAType } from './enumerations/cfa-type.model';

export interface ICFAOption {
  id?: string;
  type?: CFAType;
  description?: any;
  isDefault?: boolean;
}

export class CFAOption implements ICFAOption {
  constructor(public id?: string, public type?: CFAType, public description?: any, public isDefault?: boolean) {
    this.isDefault = this.isDefault || false;
  }
}
