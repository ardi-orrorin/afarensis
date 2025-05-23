import { z } from 'zod';
import signUpSchema from './signUpSchema';

interface RequestI {
  userId: string;
  pwd: string;
  email: string;
}

type SignUpInputT = z.infer<typeof signUpSchema.Input>;


export namespace SignUp {
  export type Request = RequestI;
  export type SignUpInput = SignUpInputT;
}