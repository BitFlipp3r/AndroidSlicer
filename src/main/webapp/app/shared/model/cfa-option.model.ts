export const enum CFAOptionType {
  ZeroCFA = 'ZeroCFA',
  ZeroOneCFA = 'ZeroOneCFA',
  VanillaZeroOneCFA = 'VanillaZeroOneCFA',
  NCFA = 'NCFA',
  VanillaNCFA = 'VanillaNCFA',
  ZeroContainerCFA = 'ZeroContainerCFA',
  ZeroOneContainerCFA = 'ZeroOneContainerCFA',
  VanillaZeroOneContainerCFA = 'VanillaZeroOneContainerCFA'
}

export interface ICFAOption {
  id?: string;
  type?: CFAOptionType;
  key?: string;
  description?: any;
  cfaLevel?: number;
  isDefault?: boolean;
}

export class CFAOption implements ICFAOption {
  constructor(
    public id?: string,
    public type?: CFAOptionType,
    public key?: string,
    public description?: any,
    public cfaLevel?: number,
    public isDefault?: boolean
  ) {
    this.isDefault = this.isDefault || false;
  }
}
