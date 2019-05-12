import { ISlicerOption } from 'app/shared/model/slicer-option.model';

export interface ISlice {
  id?: string;
  androidVersion?: number;
  androidClassName?: string;
  entryMethods?: any;
  seedStatements?: any;
  slice?: any;
  log?: any;
  threadId?: string;
  running?: boolean;
  reflectionOptions?: ISlicerOption;
  dataDependenceOptions?: ISlicerOption;
  controlDependenceOptions?: ISlicerOption;
}

export class Slice implements ISlice {
  constructor(
    public id?: string,
    public androidVersion?: number,
    public androidClassName?: string,
    public entryMethods?: any,
    public seedStatements?: any,
    public slice?: any,
    public log?: any,
    public threadId?: string,
    public running?: boolean,
    public reflectionOptions?: ISlicerOption,
    public dataDependenceOptions?: ISlicerOption,
    public controlDependenceOptions?: ISlicerOption
  ) {
    this.running = this.running || false;
  }
}
