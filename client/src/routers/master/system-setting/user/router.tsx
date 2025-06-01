import Layout from './layout';
import Index from '.';
import { CommonType } from '../../../../commons/types/commonType';
import commonFunc from '../../../../commons/services/funcs';
import ExRouteObject = CommonType.ExRouteObject;

const UserRouter: ExRouteObject = {
  id: 'root/master/system-setting/user',
  path: 'user',
  name: 'User',
  Component: Layout,
  requiredRoles: ['USER', 'ADMIN', 'MASTER'],
  loader: async () => {
    commonFunc.routeValidRoles(UserRouter);
  },
  children: [
    {
      index: true,
      Component: Index,
    },
  ],
};

export default UserRouter;
