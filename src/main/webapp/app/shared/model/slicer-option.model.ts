import { SlicerOptionType } from 'app/shared/model/enumerations/slicer-option-type.model';

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
