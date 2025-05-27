import { RouteObject } from 'react-router-dom';
import Layout from './layout';
import Index from '.';

const SignupRouter: RouteObject = {
  id: 'root/signup',
  path: 'signup',
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
