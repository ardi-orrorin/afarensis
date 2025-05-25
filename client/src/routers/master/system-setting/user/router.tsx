import { RouteObject } from 'react-router-dom';
import Layout from './layout';
import Index from '.';

const UserRouter: RouteObject = {
  id: 'root/master/system-setting/user',
  path: 'user',
  Component: Layout,
  loader: async () => {
  },
  children: [
    {
      index: true,
      Component: Index,
    },
  ],
};

export default UserRouter;
