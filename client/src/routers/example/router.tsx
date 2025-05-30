import exampleQuery from './[features]/stores/query';
import Layout from './layout';
import Index from '.';
import authMiddleware from '../../commons/services/middleware';
import { CommonType } from '../../commons/types/commonType';
import ExRouteObject = CommonType.ExRouteObject;


const ExampleRouter: ExRouteObject = {
  id: 'root/exmaple',
  path: 'example',
  name: 'Example',
  Component: Layout,
  unstable_middleware: [authMiddleware],
  shouldRevalidate: () => false,
  children: [
    {
      index: true,
      Component: Index,
      loader: async () => {
        await exampleQuery.getExample({ params: { id: 1, sort: 'asc', query: { id: 1, sort: 'asc' } } }).prefetch();
      },
    },
  ],
};

export default ExampleRouter;
