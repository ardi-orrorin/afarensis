import ExAxios from '../../../../commons/services/exAxios';
import { SignUp } from '../types/signUp';
import { CommonType } from '../../../../commons/types/commonType';
import ResponseStatus = CommonType.ResponseStatus;

const postSingUp = async (req: SignUp.Request) => {
  return ExAxios({
    method: 'POST',
    url: '/api/v1/public/users/signup',
    body: req,
  });
};

const getExistByUserId = async (userId: string) => {
  return ExAxios<ResponseStatus<boolean>>({
    method: 'GET',
    url: `/api/v1/public/users/exist-id/${userId}`,
    isReturnData: true,
  });
};

const signUpService = {
  postSingUp,
  getExistByUserId,
};

export default signUpService;