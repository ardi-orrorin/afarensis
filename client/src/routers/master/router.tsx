import Layout from './layout';
import Index from '.';
import SystemSettingRouter from './system-setting/router';
import { CommonType } from '../../commons/types/commonType';
import ExRouteObject = CommonType.ExRouteObject;

const MasterRouter: ExRouteObject = {
  id: 'root/master',
  path: 'master',
  name: 'Master',
  Component: Layout,
  // unstable_middleware: [authMiddleware],
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
