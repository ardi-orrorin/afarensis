import { z } from 'zod';
import webhookSchema from './webhookSchema';

type ResponseListT = {
  data: WebhookT[],
}

type WebhookT = InputT & {
  // id: number,
  userPk: number,
  createdAt: string,
}

type InputT = z.infer<typeof webhookSchema.Input>

export namespace WebhookType {
  export type Input = InputT;
  export type Webhook = WebhookT;
  export type ResponseList = ResponseListT;

  export enum WebhookType {
    DISCORD = 'DISCORD',
    SLACK = 'SLACK',
    TELEGRAM = 'TELEGRAM',
    GITHUB = 'GITHUB',
    NOTION = 'NOTION'
  }
}