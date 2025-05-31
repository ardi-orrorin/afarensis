import { QueryClient, queryOptions, UseQueryOptions } from '@tanstack/react-query';
import { CommonType } from '../types/commonType';
import { AxiosResponse } from 'axios';

function isAxiosResponse<T>(obj: any): obj is AxiosResponse<T> {
  return (
    obj &&
    typeof obj === 'object' &&
    'data' in obj &&
    'status' in obj &&
    'statusText' in obj &&
    'headers' in obj &&
    'config' in obj
  );
}

const queryClient = new QueryClient();

const createQueryActions = <T = any>
({
   queryKey,
   queryOp,
 }: {
  queryKey: string[];
  queryOp: Omit<UseQueryOptions<T, Error, T, string[]>, 'queryFn'>;
}): CommonType.CreateQueryActions<T> => {


  const data = queryClient.getQueryData(queryKey) as T;

  const prefetch = () => queryClient.prefetchQuery(queryOp);

  const refetch = async () => queryClient.refetchQueries({ queryKey });

  const state = queryClient.getQueryState(queryKey);

  const isPending = state?.status === 'pending';

  const isFailed = state?.status === 'error';

  const isSuccess = state?.status === 'success';

  return { prefetch, data, refetch, state, isPending, isFailed, isSuccess };
};

const baseFetchQueryFn = <R = any, A = any, D = any>
({
   queryKey,
   fetchApi,
   initialData,
   addQueryKey,
   fetchArgs,
 }: {
  queryKey: string[];
  fetchApi: (args: A) => Promise<AxiosResponse<R, D>> | Promise<R>;
  fetchArgs?: A;
  addQueryKey?: string[];
  initialData: R;
}) => {
  const queryFn = async () => {
    const res = await fetchApi(fetchArgs ?? ({} as A));

    if (isAxiosResponse(res)) {
      return res.data as R;
    } else {
      return res as R;
    }
  };

  const ojbToStr = (obj: any): string => {
    if (typeof obj === 'string' || typeof obj === 'number') {
      return String(obj);
    }

    if (Array.isArray(obj)) {
      if (obj.length === 0) {
        return ``;
      }
      return obj.map((item) => ojbToStr(item)).join(',');
    }

    if (typeof obj === 'object') {
      return Object.entries(obj)
        .map(([key, value]) => {
          if (typeof value === 'object') {
            return `{${key}:${ojbToStr(value)}}`;
          }

          return `${key}:${value}`;
        })
        .reduce((acc, cur) => (cur === '' ? acc : `${acc},${cur}`));
    }

    return '';
  };

  if (addQueryKey && addQueryKey.length > 0) {
    queryKey.push(...addQueryKey);
  } else if (fetchArgs) {
    queryKey.push(`args:${ojbToStr(fetchArgs)}`);
  }

  const queryOp = queryOptions({
    queryFn,
    queryKey,
    initialData,
  });

  const actions = reactQuery.createQueryActions<R>({ queryKey, queryOp });

  return { queryKey, ...actions, queryOp } as CommonType.GetQuery<R>;
};

const reactQuery = {
  queryClient,
  createQueryActions,
  baseFetchQueryFn,
};

export default reactQuery;
