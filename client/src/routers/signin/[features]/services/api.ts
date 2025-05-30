import { SignIn } from '../types/signin';
import ExAxios from '../../../../commons/services/exAxios';
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


const signInService = {
  postSignIn,
  postResetPassword,
};

export default signInService;