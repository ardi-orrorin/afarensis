import exampleServiceApi from '../services/api';
import { ExampleType } from '../types/example';
import reactQuery from '../../../../commons/services/reractQuery';

interface Args {
  params: { id: number; sort: string; query: { id: number; sort: string } };
}

const getExample = (fetchArgs: Args) => {
  type Data = ExampleType.ExampleApi;

  const queryKey = ['example'];

  return reactQuery.baseFetchQueryFn<Data, Args>({
    queryKey,
    fetchApi: exampleServiceApi.getExample,
    fetchArgs,
    initialData: {} as Data,
  });
};

const exampleQuery = {
  getExample,
};

export default exampleQuery;
