import { SignIn } from '../types/signin';
import ExAxios from '../../../../commons/services/exAxios';
import exAxios from '../../../../commons/services/exAxios';
import { FindPasswordType } from '../types/findPassword';
import { CommonType } from '../../../../commons/types/commonType';
import ResponseStatus = CommonType.ResponseStatus;

const postSignIn = async (req: SignIn.Request) => {
  return ExAxios<SignIn.Token>({
    method: 'POST',
    url: '/api/v1/public/users/signin',
    body: req,
    isReturnData: true,
  });
};

const postResetPassword = async (req: FindPasswordType.Input) => {
  return ExAxios<ResponseStatus<boolean>>({
    method: 'POST',
    url: '/api/v1/public/users/reset-password',
    body: req,
    isReturnData: true,
  });
};

const getAssertion = async ({ userId }: { userId: string }) => {
  return exAxios<ResponseStatus<string>>({
    method: 'GET',
    params: { userId },
    url: 'api/v1/public/users/signin/passkey-start',
    isReturnData: true,
  });
};


const signInService = {
  postSignIn,
  postResetPassword,
  getAssertion,
};

export default signInService;