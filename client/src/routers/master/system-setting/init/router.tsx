import Layout from './layout';
import Index from '.';
import { CommonType } from '../../../../commons/types/commonType';
import commonFunc from '../../../../commons/services/funcs';
import systemSettingQuery from '../[features]/stores/query';
import ExRouteObject = CommonType.ExRouteObject;

const InitRouter: ExRouteObject = {
  id: 'root/master/system-setting/init',
  path: 'init',
  name: 'Init',
  requiredRoles: ['USER', 'ADMIN', 'MASTER'],
  Component: Layout,
  loader: async () => {
    commonFunc.routeValidRoles(InitRouter);
    await systemSettingQuery.privateQuery().prefetch();
  },
  children: [
    {
      index: true,
      Component: Index,
    },
  ],
};

export default InitRouter;
