import Layout from './layout';
import Index from '.';
import commonFunc from '../../../commons/services/funcs';
import { CommonType } from '../../../commons/types/commonType';
import passkeyQuery from './[features]/stores/query';
import ExRouteObject = CommonType.ExRouteObject;

const PasskeyRouter: ExRouteObject = {
  id: 'root/user/passkey',
  path: 'passkey',
  name: 'Passkey',
  requiredRoles: ['USER'],
  Component: Layout,
  loader: async function() {
    await passkeyQuery.passkey().prefetch();
    commonFunc.routeValidRoles(PasskeyRouter);
  },
  children: [
    {
      index: true,
      Component: Index,
    },
  ],
};


export default PasskeyRouter;
