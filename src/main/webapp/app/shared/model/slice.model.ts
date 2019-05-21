export const enum ReflectionOptions {
  FULL = 'FULL',
  NO_FLOW_TO_CASTS = 'NO_FLOW_TO_CASTS',
  NO_FLOW_TO_CASTS_NO_METHOD_INVOKE = 'NO_FLOW_TO_CASTS_NO_METHOD_INVOKE',
  NO_METHOD_INVOKE = 'NO_METHOD_INVOKE',
  NO_STRING_CONSTANTS = 'NO_STRING_CONSTANTS',
  NONE = 'NONE',
  ONE_FLOW_TO_CASTS_NO_METHOD_INVOKE = 'ONE_FLOW_TO_CASTS_NO_METHOD_INVOKE'
}

export const enum DataDependenceOptions {
  FULL = 'FULL',
  NO_BASE_NO_EXCEPTIONS = 'NO_BASE_NO_EXCEPTIONS',
  NO_BASE_NO_HEAP = 'NO_BASE_NO_HEAP',
  NO_BASE_NO_HEAP_NO_EXCEPTIONS = 'NO_BASE_NO_HEAP_NO_EXCEPTIONS',
  NO_BASE_PTRS = 'NO_BASE_PTRS',
  NO_EXCEPTIONS = 'NO_EXCEPTIONS',
  NO_HEAP = 'NO_HEAP',
  NO_HEAP_NO_EXCEPTIONS = 'NO_HEAP_NO_EXCEPTIONS',
  NONE = 'NONE',
  REFLECTION = 'REFLECTION'
}

export const enum ControlDependenceOptions {
  FULL = 'FULL',
  NO_EXCEPTIONAL_EDGES = 'NO_EXCEPTIONAL_EDGES',
  NONE = 'NONE'
}

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
  reflectionOptions?: ReflectionOptions;
  dataDependenceOptions?: DataDependenceOptions;
  controlDependenceOptions?: ControlDependenceOptions;
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
    public reflectionOptions?: ReflectionOptions,
    public dataDependenceOptions?: DataDependenceOptions,
    public controlDependenceOptions?: ControlDependenceOptions
  ) {
    this.running = this.running || false;
  }
}
