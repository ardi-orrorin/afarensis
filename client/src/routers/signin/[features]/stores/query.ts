import { SignIn } from '../types/signin';
import reactQuery from '../../../../commons/services/reractQuery';
import signInService from '../services/api';

const signIn = (fetchArgs: SignIn.Request) => {
  type Data = SignIn.Token;

  const queryKey = ['signin'];
  
  return reactQuery.baseFetchQueryFn<Data, SignIn.Request>({
    queryKey,
    fetchApi: signInService.postSignIn,
    fetchArgs,
    initialData: {} as Data,
  });

};


const signInQuery = {
  signIn,
};

export default signInQuery;