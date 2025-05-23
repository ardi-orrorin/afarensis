import { CommonType } from '../../../../commons/types/commonType';

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


export namespace SignIn {
  export type Request = RequestI;
  export type Token = TokenI;
}