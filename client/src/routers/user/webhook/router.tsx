import Layout from './layout';
import Index from '.';
import { CommonType } from '../../../commons/types/commonType';
import webhookQuery from './[features]/stores/query';
import ExRouteObject = CommonType.ExRouteObject;

const WebhookRouter: ExRouteObject = {
  id: 'root/user/webhook',
  path: 'webhook',
  name: 'Webhook',
  Component: Layout,
  loader: async () => {
    await webhookQuery.webhook().prefetch();
  },
  children: [
    {
      index: true,
      Component: Index,
    },

  ],
};

export default WebhookRouter;
