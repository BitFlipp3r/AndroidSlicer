import { CFAType } from './cfa-option.model';

export const enum ReflectionOptions {
  FULL = 'FULL',
  APPLICATION_GET_METHOD = 'APPLICATION_GET_METHOD',
  NO_FLOW_TO_CASTS = 'NO_FLOW_TO_CASTS',
  NO_FLOW_TO_CASTS_APPLICATION_GET_METHOD = 'NO_FLOW_TO_CASTS_APPLICATION_GET_METHOD',
  NO_METHOD_INVOKE = 'NO_METHOD_INVOKE',
  NO_FLOW_TO_CASTS_NO_METHOD_INVOKE = 'NO_FLOW_TO_CASTS_NO_METHOD_INVOKE',
  ONE_FLOW_TO_CASTS_NO_METHOD_INVOKE = 'ONE_FLOW_TO_CASTS_NO_METHOD_INVOKE',
  ONE_FLOW_TO_CASTS_APPLICATION_GET_METHOD = 'ONE_FLOW_TO_CASTS_APPLICATION_GET_METHOD',
  MULTI_FLOW_TO_CASTS_APPLICATION_GET_METHOD = 'MULTI_FLOW_TO_CASTS_APPLICATION_GET_METHOD',
  NO_STRING_CONSTANTS = 'NO_STRING_CONSTANTS',
  STRING_ONLY = 'STRING_ONLY',
  NONE = 'NONE'
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

export interface ISlicedClass {
  className?: string;
  packagePath?: string;
  code?: string;
}

export class SlicedClass implements ISlicedClass {
  constructor(public className?: string, public packagePath?: string, public code?: string) {}
}

export interface ISlice {
  id?: string;
  androidVersion?: number;
  androidClassName?: string;
  entryMethods?: string[];
  seedStatements?: string[];
  slicedClasses?: ISlicedClass[];
  log?: any;
  threadId?: string;
  running?: boolean;
  cfaType?: CFAType;
  cfaLevel?: number;
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
    public cfaType?: CFAType,
    public cfaLevel?: number,
    public reflectionOptions?: ReflectionOptions,
    public dataDependenceOptions?: DataDependenceOptions,
    public controlDependenceOptions?: ControlDependenceOptions
  ) {
    this.running = this.running || false;
  }
}
