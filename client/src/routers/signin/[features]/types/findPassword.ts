import { z } from 'zod';
import findPasswordSchema from './findPasswordSchema';

type InputT = z.infer<typeof findPasswordSchema.Input>

export namespace FindPasswordType {
  export type Input = InputT;
}