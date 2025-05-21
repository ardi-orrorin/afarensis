import { QueryState } from '@tanstack/react-query';

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
};

type HistoryPageT = {
  id: string;
  title: string;
  path: string;
  img?: string;
  isOpen: boolean;
};

export namespace CommonType {
  export type CreateQueryActions<T> = CreateQueryActionsT<T>;
  export type GetQuery<T> = GetQueryT<T>;
  export type HistoryPage = HistoryPageT;
  export type HistoryPages = HistoryPageT[];
}
