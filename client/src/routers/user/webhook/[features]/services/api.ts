import ExAxios from '../../../../../commons/services/exAxios';
import { WebhookType } from '../types/webhook';
import { CommonType } from '../../../../../commons/types/commonType';
import ResponseStatus = CommonType.ResponseStatus;

const getWebhook = async () => {
  return ExAxios<WebhookType.ResponseList>({
    method: 'GET',
    url: '/api/v1/private/user/webhook',
    isReturnData: true,
  })
    .then((res) => res.data);
};

const postWebhook = async (body: WebhookType.Input) => {
  return ExAxios<ResponseStatus<boolean>, WebhookType.Input>({
    method: 'POST',
    url: '/api/v1/private/user/webhook',
    body,
    isReturnData: true,
  });
};

const deleteWebhook = async (id: number) => {
  return ExAxios<ResponseStatus<boolean>>({
    method: 'DELETE',
    url: `/api/v1/private/user/webhook/${id}`,
    isReturnData: true,
  });
};

const patchWebhook = async (body: WebhookType.Input) => {
  return ExAxios<ResponseStatus<boolean>, WebhookType.Input>({
    method: 'PATCH',
    url: `/api/v1/private/user/webhook`,
    body,
    isReturnData: true,
  });
};

const webhookServiceApi = {
  getWebhook,
  postWebhook,
  deleteWebhook,
  patchWebhook,
};

export default webhookServiceApi;