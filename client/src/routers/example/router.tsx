import { RouteObject } from 'react-router-dom';
import exampleQuery from './[features]/stores/query';
import Layout from './layout';
import Index from '.';
import authMiddleware from '../../commons/services/middleware';


const ExampleRouter: RouteObject = {
  id: 'root/exmaple',
  path: 'example',
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
