import { QueryState, UseQueryOptions } from '@tanstack/react-query';
import { IndexRouteObject, RouteObject } from 'react-router-dom';

type CreateQueryActionsT<T> = {
  prefetch: () => Promise<void>;
  data: T;
  refetch: () => Promise<void>;
  state: QueryState<unknown, Error> | undefined;
  isPending: boolean;
  isFailed: boolean;
  isSuccess: boolean;
};

type GetQueryT<T> = CreateQueryActionsT<T> & {
  queryKey: string[];
  queryOp: Omit<UseQueryOptions<T, Error, T, string[]>, 'queryFn'>;
};

type HistoryPageT = {
  id: string;
  title: string;
  path: string;
  img?: string;
  isOpen: boolean;
};

type ResponseStatusT<T> = {
  status: CommonType.ResStatus;
  message: string;
  data: T;
};

type PageResponseT<T> = {
  page: number;
  size: number;
  total: number;
  totalPage: number;
  isFirst: boolean;
  isLast: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  data: T[];
}

type PublishRefreshTokenT = {
  refreshToken: string;
  userId: string;
}

type FormErrorsT<T> = {
  [key in keyof T]?: string | string[];
} & {
  [key: string]: string [] | undefined;
}

interface RoutePathObjectI {
  path: string;
  name: string;
  requiredRoles: (keyof typeof CommonType.Role)[];
  children?: RoutePathObjectI[];
}

type ExRouteObjectT = RouteObject & {
  name: string;
  requiredRoles: (keyof typeof CommonType.Role)[];
}

type ExIndexRouteObjectT = IndexRouteObject & ExRouteObjectT


export namespace CommonType {
  export type CreateQueryActions<T> = CreateQueryActionsT<T>;
  export type GetQuery<T> = GetQueryT<T>;
  export type HistoryPage = HistoryPageT;
  export type HistoryPages = HistoryPageT[];
  export type ResponseStatus<T> = ResponseStatusT<T>;
  export type PublishRefreshToken = PublishRefreshTokenT;
  export type FormErrors<T> = FormErrorsT<T>;
  export type RoutePathObject = RoutePathObjectI;
  export type ExRouteObject = ExRouteObjectT;
  export type ExIndexRouteObject = ExIndexRouteObjectT;
  export type PageResponse<T> = PageResponseT<T>;

  export enum ResStatus {
    SUCCESS = 'SUCCESS',
    FAILED = 'FAILED',
    ERROR = 'ERROR',
    NOT_FOUND = 'NOT_FOUND',
    BAD_REQUEST = 'BAD_REQUEST',
    UNAUTHORIZED = 'UNAUTHORIZED',
    FORBIDDEN = 'FORBIDDEN',
    INTERNAL_SERVER_ERROR = 'INTERNAL_SERVER_ERROR',
    SKIP = 'SKIP'
  }

  export enum Role {
    ADMIN = 'ADMIN',
    USER = 'USER',
    GUEST = 'GUEST',
    MASTER = 'MASTER'
  }
}
