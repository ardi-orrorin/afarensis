import { CommonType } from '../../../../commons/types/commonType';
import { z } from 'zod';
import signInSchema from './signInSchema';

interface RequestI {
  userId: string,
  pwd: string,
}

interface TokenI {
  accessToken: string;
  accessTokenExpiresIn: number;
  refreshToken: string;
  refreshTokenExpiresIn: number;
  userId: string;
  roles: CommonType.Role[];
}

type InputT = z.infer<typeof signInSchema.Input>;

export namespace SignIn {
  export type Request = RequestI;
  export type Token = TokenI;
  export type Input = InputT;
}