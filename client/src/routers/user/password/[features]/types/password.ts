import { z } from 'zod';
import passwordSchema from './passwordSchema';

type PasswordT = z.infer<typeof passwordSchema.Input>

export namespace PasswordType {
  export type Password = PasswordT;
}