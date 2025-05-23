import { RouteObject } from 'react-router-dom';
import Layout from './layout';
import Index from '.';

const SignInRouter: RouteObject = {
  id: 'root/signin',
  path: '/signin',
  Component: Layout,
  children: [
    {
      index: true,
      Component: Index,
    },
  ],
};

export default SignInRouter;
