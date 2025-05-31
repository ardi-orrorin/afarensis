import { WebhookType } from '../types/webhook';
import reactQuery from '../../../../../commons/services/reractQuery';
import webhookServiceApi from '../services/api';

const webhook = () => {
  const queryKey = ['webhook'];

  return reactQuery.baseFetchQueryFn<WebhookType.Webhook[]>({
    queryKey,
    fetchApi: webhookServiceApi.getWebhook,
    initialData: [] as WebhookType.Webhook[],
  });
};

const webhookQuery = {
  webhook,
};

export default webhookQuery;