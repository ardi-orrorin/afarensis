import { RouteObject } from 'react-router-dom';
import Layout from './layout';
import Index from '.';
import systemSettingQuery from './[features]/stores/query';
import SmtpRouter from './smtp/router';
import UserRouter from './user/router';

const SystemSettingRouter: RouteObject = {
  id: 'root/master/system-setting',
  path: 'system-setting',
  Component: Layout,
  loader: async () => {
    await systemSettingQuery.privateQuery().prefetch();
  },
  children: [
    {
      index: true,
      Component: Index,
    },
    SmtpRouter,
    UserRouter,
  ],
};

export default SystemSettingRouter;
