import Layout from './layout';
import Index from '.';
import { CommonType } from '../../../commons/types/commonType';
import commonFunc from '../../../commons/services/funcs';
import ExRouteObject = CommonType.ExRouteObject;

const PasswordRouter: ExRouteObject = {
  id: 'root/user/password',
  path: 'password',
  name: 'Update Password',
  requiredRoles: ['USER'],
  Component: Layout,
  loader: async () => {
    commonFunc.routeValidRoles(PasswordRouter);
  },
  children: [
    {
      index: true,
      Component: Index,
    },

  ],
};

export default PasswordRouter;
