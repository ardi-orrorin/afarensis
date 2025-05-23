import ExAxios from '../../../../commons/services/exAxios';
import { SignUp } from '../types/singUp';

const postSingUp = async (req: SignUp.Request) => {
  return ExAxios({
    method: 'POST',
    url: '/api/v1/public/users/signup',
    body: req,
  });
};

const signUpService = {
  postSingUp,
};

export default signUpService;