import { CommonType } from '../types/commonType';
import { AxiosError, AxiosResponse } from 'axios';
import { Dispatch } from 'react';
import ResStatus = CommonType.ResStatus;

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


const getAllRoutePaths = (routes: CommonType.ExRouteObject | CommonType.ExIndexRouteObject, parentPath = ''): CommonType.RoutePathObject[] => {
  if (!routes.children) return [];

  return (routes.children as CommonType.ExRouteObject[])
    .filter((route: CommonType.ExRouteObject | CommonType.ExIndexRouteObject) => !route.index)
    .map((route: CommonType.ExRouteObject | CommonType.ExIndexRouteObject) => {
      const currentPath = parentPath + '/' + (route.path ?? '');
      const children = getAllRoutePaths(route, currentPath);

      return {
        path: currentPath,
        name: route.name ?? '',
        ...(children.length > 0 && { children }),
      };
    });
};

const setResponseError = (
  err: AxiosError<unknown, any>,
  setResponse: Dispatch<React.SetStateAction<CommonType.ResponseStatus<boolean>>>,
) => {
  const res = err.response as AxiosResponse;
  setResponse({
    status: ResStatus.ERROR,
    message: res.data.message ?? '',
    data: false,
  });
};

const commonFunc = {
  subtractRequiredStr,
  axiosError,
  getAllRoutePaths,
  setResponseError,
};

export default commonFunc;