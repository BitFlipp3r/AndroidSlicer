import { ISlicerOption } from 'app/shared/model/slicer-option.model';

export interface ISlice {
  id?: string;
  androidVersion?: number;
  androidClassName?: string;
  entryMethods?: string[];
  seedStatements?: string[];
  slice?: any;
  log?: any;
  threadId?: string;
  running?: boolean;
  reflectionOption?: ISlicerOption;
  dataDependenceOption?: ISlicerOption;
  controlDependenceOption?: ISlicerOption;
}

export class Slice implements ISlice {
  constructor(
    public id?: string,
    public androidVersion?: number,
    public androidClassName?: string,
    public entryMethods = [],
    public seedStatements = [],
    public slice?: any,
    public log?: any,
    public threadId?: string,
    public running?: boolean,
    public reflectionOption?: ISlicerOption,
    public dataDependenceOption?: ISlicerOption,
    public controlDependenceOption?: ISlicerOption
  ) {
    this.running = this.running || false;
  }
}
