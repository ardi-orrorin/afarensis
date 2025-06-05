import Layout from './layout';
import Index from '.';
import PasswordRouter from './password/router';
import { CommonType } from '../../commons/types/commonType';
import WebhookRouter from './webhook/router';
import commonFunc from '../../commons/services/funcs';
import PasskeyRouter from './passkey/router';
import ExRouteObject = CommonType.ExRouteObject;

const UserRouter: ExRouteObject = {
  id: 'root/user',
  path: 'user',
  name: 'User',
  requiredRoles: ['USER'],
  Component: Layout,
  loader: async function() {
    commonFunc.routeValidRoles(UserRouter);
  },
  children: [
    {
      index: true,
      Component: Index,
    },
    PasswordRouter,
    WebhookRouter,
    PasskeyRouter,
  ],
};


export default UserRouter;
