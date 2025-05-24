import { RouteObject } from 'react-router-dom';
import Layout from './layout';
import Index from '.';
import systemSettingQuery from './[features]/stores/query';
import SmtpRouter from './smtp/router';

const SystemSettingRouter: RouteObject = {
  id: 'root/master/system-setting',
  path: 'system-setting',
  Component: Layout,
  loader: async () => {
    await Promise.allSettled([
      systemSettingQuery.publicQuery().prefetch(),
      systemSettingQuery.privateQuery().prefetch(),
    ]);
  },
  children: [
    {
      index: true,
      Component: Index,
    },
    SmtpRouter,
  ],
};

export default SystemSettingRouter;
