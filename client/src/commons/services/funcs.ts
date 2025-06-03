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
  if (!err.status) return;
  throw new Error(convertErrorStatusToMessage(err.status));
};

const convertErrorStatusToMessage = (status: number) => {
  switch (status) {
    case 400:
      return '잘못된 요청입니다.';
    case 401:
      return '로그인 정보가 만료되었습니다. 다시 로그인해주세요.';
    case 403:
      return '접근 권한이 없습니다.';
    case 404:
      return '요청하신 페이지를 찾을 수 없습니다.';
    case 405:
      return '허용되지 않은 메서드입니다.';
    case 500:
      return '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
    default:
      return '알 수 없는 오류가 발생했습니다.';
  }
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
  const roles = fromBase64(cookie).split(':');

  if (!validRoles({ requiredRoles: router.requiredRoles, userRoles: roles })) {
    throw new Error('접속 권한이 없습니다.');
  }
};

function fromBase64(base64Str: string) {
  const bin = atob(base64Str);
  const bytes = Uint8Array.from(bin, c => c.charCodeAt(0));
  return new TextDecoder().decode(bytes); // UTF-8 디코딩
}

const commonFunc = {
  subtractRequiredStr,
  axiosError,
  getAllRoutePaths,
  setResponseError,
  validRoles,
  routeValidRoles,
  fromBase64,
};

export default commonFunc;