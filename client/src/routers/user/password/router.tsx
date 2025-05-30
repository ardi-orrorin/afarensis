import Layout from './layout';
import Index from '.';
import { CommonType } from '../../../commons/types/commonType';
import ExRouteObject = CommonType.ExRouteObject;

const PasswordRouter: ExRouteObject = {
  id: 'root/user/password',
  path: 'password',
  name: 'Update Password',
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

export default PasswordRouter;
