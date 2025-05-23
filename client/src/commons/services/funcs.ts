import { CommonType } from '../types/commonType';

const subtractRequiredStr =
  <T extends Object>(obj: T) =>
    Object.entries(obj)
      .filter(([key, value]) => {
        return value[0] !== 'Required';
      })
      .reduce((acc, [key, value]) => {
        acc[key as keyof T] = value;
        return acc;
      }, {} as CommonType.FormErrors<T>);


const commonFunc = {
  subtractRequiredStr,
};

export default commonFunc;