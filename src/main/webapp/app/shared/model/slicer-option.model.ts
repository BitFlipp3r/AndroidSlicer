export const enum SlicerOptionType {
  REFLECTION_OPTION = 'REFLECTION_OPTION',
  DATA_DEPENDENCE_OPTION = 'DATA_DEPENDENCE_OPTION',
  CONTROL_DEPENDENCE_OPTION = 'CONTROL_DEPENDENCE_OPTION'
}

export interface ISlicerOption {
  id?: string;
  type?: SlicerOptionType;
  key?: string;
  description?: any;
  isDefault?: boolean;
}

export class SlicerOption implements ISlicerOption {
  constructor(
    public id?: string,
    public type?: SlicerOptionType,
    public key?: string,
    public description?: any,
    public isDefault?: boolean
  ) {
    this.isDefault = this.isDefault || false;
  }
}
