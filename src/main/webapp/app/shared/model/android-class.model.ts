export interface IAndroidClass {
  name?: string;
  path?: string;
}

export class AndroidClass implements IAndroidClass {
  constructor(public name?: string, public path?: string) {}
}
