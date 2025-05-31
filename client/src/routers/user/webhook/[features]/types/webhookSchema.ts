import { z } from 'zod';
import { WebhookType } from './webhook';

const Input = z.object({
  id: z.number().optional(),
  type: z.nativeEnum(WebhookType.WebhookType, {
    required_error: 'Type is required',
    description: 'The type of webhook',
    invalid_type_error: 'Invalid type',
    message: `Invalid type. Must be one of ${Object.values(WebhookType.WebhookType).join(', ')}`,
  }),
  url: z.string().min(1, 'URL is required').url('Invalid URL').startsWith('https://', 'URL must start with https://'),
  secret: z.string().optional(),
});

const webhookSchema = {
  Input,
};

export default webhookSchema;