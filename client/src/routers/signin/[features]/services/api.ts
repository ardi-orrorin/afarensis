import { SignIn } from '../types/signin';
import ExAxios from '../../../../commons/services/exAxios';

const postSignIn = async (req: SignIn.Request) => {
  return ExAxios<SignIn.Token>({
    method: 'POST',
    url: '/api/v1/public/users/signin',
    body: req,
    isReturnData: true,
  });
};


const signInService = {
  postSignIn,
};

export default signInService;