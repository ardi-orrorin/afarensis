import Layout from './layout';
import Index from '.';
import { CommonType } from '../../commons/types/commonType';
import ExRouteObject = CommonType.ExRouteObject;

const SignInRouter: ExRouteObject = {
  id: 'root/signin',
  path: 'signin',
  name: 'Sign In',
  requiredRoles: [],
  Component: Layout,
  children: [
    {
      index: true,
      Component: Index,
    },
  ],
};

export default SignInRouter;
