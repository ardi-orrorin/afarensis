import { CommonType } from '../types/commonType';
import { AxiosError, AxiosResponse } from 'axios';
import { Dispatch } from 'react';
import { Cookies } from 'react-cookie';
import ResStatus = CommonType.ResStatus;
import ExRouteObject = CommonType.ExRouteObject;
import ExIndexRouteObject = CommonType.ExIndexRouteObject;

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
        requiredRoles: route.requiredRoles ?? [],
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

const validRoles =
  ({ requiredRoles, userRoles }: {
    requiredRoles: (keyof typeof CommonType.Role)[],
    userRoles: string[]
  }) => {
    if (requiredRoles.length === 0) return true;
    return requiredRoles.every((role) => userRoles.includes(role));
  };

const routeValidRoles = (router: ExRouteObject | ExIndexRouteObject) => {
  const cookie = new Cookies().get('roles') as string;
  const roles = cookie.split(':');
  
  if (!validRoles({ requiredRoles: router.requiredRoles, userRoles: roles })) {
    throw new Error('접속 권한이 없습니다.');
  }
};

const commonFunc = {
  subtractRequiredStr,
  axiosError,
  getAllRoutePaths,
  setResponseError,
  validRoles,
  routeValidRoles,
};

export default commonFunc;