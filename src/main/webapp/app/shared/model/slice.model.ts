import { ReflectionOptions } from 'app/shared/model/enumerations/reflection-options.model';
import { DataDependenceOptions } from 'app/shared/model/enumerations/data-dependence-options.model';
import { ControlDependenceOptions } from 'app/shared/model/enumerations/control-dependence-options.model';

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
  reflectionOptions?: ReflectionOptions;
  dataDependenceOptions?: DataDependenceOptions;
  controlDependenceOptions?: ControlDependenceOptions;
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
    public reflectionOptions?: ReflectionOptions,
    public dataDependenceOptions?: DataDependenceOptions,
    public controlDependenceOptions?: ControlDependenceOptions
  ) {
    this.running = this.running || false;
  }
}
