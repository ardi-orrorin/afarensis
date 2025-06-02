import Layout from './layout';
import Index from '.';
import systemSettingQuery from './[features]/stores/query';
import SmtpRouter from './smtp/router';
import UserRouter from './user/router';
import { CommonType } from '../../../commons/types/commonType';
import commonFunc from '../../../commons/services/funcs';
import InitRouter from './init/router';
import ExRouteObject = CommonType.ExRouteObject;

const SystemSettingRouter: ExRouteObject = {
  id: 'root/master/system-setting',
  path: 'system-setting',
  name: 'System-Setting',
  requiredRoles: ['USER', 'ADMIN', 'MASTER'],
  Component: Layout,
  loader: async () => {
    commonFunc.routeValidRoles(SystemSettingRouter);
    await systemSettingQuery.privateQuery().prefetch();
  },
  children: [
    {
      index: true,
      Component: Index,
    },
    SmtpRouter,
    UserRouter,
    InitRouter,
  ],
};

export default SystemSettingRouter;
