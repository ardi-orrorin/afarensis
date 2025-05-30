import Layout from './layout';
import Index from '.';
import { CommonType } from '../../commons/types/commonType';
import ExRouteObject = CommonType.ExRouteObject;

const SignupRouter: ExRouteObject = {
  id: 'root/signup',
  path: 'signup',
  name: 'Sign Up',
  Component: Layout,
  loader: async () => {
  },
  children: [
    {
      index: true,
      Component: Index,
    },
  ],
};

export default SignupRouter;
