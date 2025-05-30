import Layout from './layout';
import Index from '.';
import { CommonType } from '../../../../commons/types/commonType';
import ExRouteObject = CommonType.ExRouteObject;

const UserRouter: ExRouteObject = {
  id: 'root/master/system-setting/user',
  path: 'user',
  name: 'User',
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
