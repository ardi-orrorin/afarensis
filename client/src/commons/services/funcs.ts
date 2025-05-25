import { CommonType } from '../types/commonType';
import { AxiosError } from 'axios';

const subtractRequiredStr =
  <T extends Object>(obj: T) =>
    Object.entries(obj)
      .filter(([_, value]) => {
        return value[0] !== 'Required';
      })
      .reduce((acc, [key, value]) => {
          acc[key as keyof T] = value;
          return acc;
        }, {} as CommonType.FormErrors<T>,
      );

const axiosError = (err: AxiosError) => {
  console.log(err);
};

const commonFunc = {
  subtractRequiredStr,
  axiosError,
};

export default commonFunc;