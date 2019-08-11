export const enum CFAType {
  ZERO_CFA = 'ZERO_CFA',
  ZERO_ONE_CFA = 'ZERO_ONE_CFA',
  VANILLA_ZERO_ONE_CFA = 'VANILLA_ZERO_ONE_CFA',
  N_CFA = 'N_CFA',
  VANILLA_N_CFA = 'VANILLA_N_CFA',
  ZERO_CONTAINER_CFA = 'ZERO_CONTAINER_CFA',
  ZERO_ONE_CONTAINER_CFA = 'ZERO_ONE_CONTAINER_CFA',
  VANILLA_ZERO_ONE_CONTAINER_CFA = 'VANILLA_ZERO_ONE_CONTAINER_CFA'
}

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
