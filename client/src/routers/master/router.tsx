import Layout from './layout';
import Index from '.';
import SystemSettingRouter from './system-setting/router';
import { CommonType } from '../../commons/types/commonType';
import commonFunc from '../../commons/services/funcs';
import ExRouteObject = CommonType.ExRouteObject;

const MasterRouter: ExRouteObject = {
  id: 'root/master',
  path: 'master',
  name: 'Master',
  requiredRoles: ['USER', 'ADMIN', 'MASTER'],
  Component: Layout,
  // unstable_middleware: [authMiddleware],
  loader: async () => {
    commonFunc.routeValidRoles(MasterRouter);
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
