import Layout from './layout';
import Index from '.';
import { CommonType } from '../../../../commons/types/commonType';
import commonFunc from '../../../../commons/services/funcs';
import ExRouteObject = CommonType.ExRouteObject;

const SmtpRouter: ExRouteObject = {
  id: 'root/master/system-setting/smtp',
  path: 'smtp',
  name: 'Smtp',
  requiredRoles: ['USER', 'ADMIN', 'MASTER'],
  Component: Layout,
  loader: async () => {
    commonFunc.routeValidRoles(SmtpRouter);
  },
  children: [
    {
      index: true,
      Component: Index,
    },
  ],
};

export default SmtpRouter;
