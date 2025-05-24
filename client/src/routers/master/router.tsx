import { RouteObject } from 'react-router-dom';
import Layout from './layout';
import Index from '.';
import SystemSettingRouter from './system-setting/router';
import authMiddleware from '../../commons/services/middleware';

const MasterRouter: RouteObject = {
  id: 'root/master',
  path: '/master',
  Component: Layout,
  unstable_middleware: [authMiddleware],
  loader: async () => {
  },
  children: [
    {
      index: true,
      Component: Index,
    },
    SystemSettingRouter,
  ],
};

export default MasterRouter;
