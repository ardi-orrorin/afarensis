import Layout from './layout';
import Index from '.';
import { CommonType } from '../../../commons/types/commonType';
import commonFunc from '../../../commons/services/funcs';
import ExRouteObject = CommonType.ExRouteObject;

const OtpRouter: ExRouteObject = {
  id: 'root/user.otp',
  path: 'otp',
  name: 'Otp',
  requiredRoles: ['USER'],
  Component: Layout,
  loader: async function() {
    commonFunc.routeValidRoles(OtpRouter);
  },
  children: [
    {
      index: true,
      Component: Index,
    },
  ],
};


export default OtpRouter;
