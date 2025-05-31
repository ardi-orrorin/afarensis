import Layout from './layout';
import Index from '.';
import PasswordRouter from './password/router';
import { CommonType } from '../../commons/types/commonType';
import WebhookRouter from './webhook/router';
import ExRouteObject = CommonType.ExRouteObject;

const UserRouter: ExRouteObject = {
  id: 'root/user',
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
    PasswordRouter,
    WebhookRouter,
  ],
};

export default UserRouter;
