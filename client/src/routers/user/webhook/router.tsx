import Layout from './layout';
import Index from '.';
import { CommonType } from '../../../commons/types/commonType';
import webhookQuery from './[features]/stores/query';
import commonFunc from '../../../commons/services/funcs';
import ExRouteObject = CommonType.ExRouteObject;

const WebhookRouter: ExRouteObject = {
  id: 'root/user/webhook',
  path: 'webhook',
  name: 'Webhook',
  requiredRoles: [],
  Component: Layout,
  loader: async function() {
    await webhookQuery.webhook().prefetch();
    commonFunc.routeValidRoles(WebhookRouter);
  },
  children: [
    {
      index: true,
      Component: Index,
    },
  ],
};

export default WebhookRouter;
