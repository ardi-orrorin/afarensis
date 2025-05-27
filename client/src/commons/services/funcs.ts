import { CommonType } from '../types/commonType';
import { AxiosError } from 'axios';
import { IndexRouteObject, RouteObject } from 'react-router-dom';

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


const getAllRoutePaths = (routes: RouteObject | IndexRouteObject, parentPath = ''): CommonType.RoutePathObject[] => {
  if (!routes.children) return [];

  return routes.children
    .filter((route: RouteObject | IndexRouteObject) => !route.index)
    .map((route: RouteObject | IndexRouteObject) => {
      const currentPath = parentPath + '/' + (route.path ?? '');
      const children = getAllRoutePaths(route, currentPath);

      return {
        path: currentPath,
        name: currentPath.split('/').pop() ?? '',
        ...(children.length > 0 && { children }),
      };
    });
};

const commonFunc = {
  subtractRequiredStr,
  axiosError,
  getAllRoutePaths,
};

export default commonFunc;